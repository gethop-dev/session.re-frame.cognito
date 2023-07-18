;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.token
  (:require [dev.gethop.session.re-frame.cognito :as session.cognito]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [re-frame.core :as rf]))

(def session-error-events
  "Collection of events to dispatch on session error"
  [[::user-logout]])

(rf/reg-sub
 ::id-token
 (fn [db _]
   (::id-token db)))

(rf/reg-event-fx
 ::set-id-token
 (fn [{:keys [db]} [_ id-token]]
   {:db (assoc db ::id-token id-token)}))

(rf/reg-event-db
 ::remove-token
 (fn [db _]
   (dissoc db ::id-token)))

(defn- session-error
  []
  (doseq [event session-error-events]
    (rf/dispatch event)))

(defn- get-user-session
  [current-user]
  (.getSession current-user
               (fn [err session]
                 (if err
                   (do
                     (session-error)
                     ;; Make sure we return nil to signal there is no active session.
                     nil)
                   session))))

(defn- session-cofx
  [{:keys [db user-pool] :as cofx} _]
  {:pre [(or user-pool (::session.cognito/config db))]
   :post [(contains? % :session)]}
  (rf/console :log "Calculating session cofx" (clj->js cofx))
  (let [user-pool (or user-pool (user-pool/get-user-pool db))
        session (when-let [current-user (.getCurrentUser user-pool)]
                  (when-let [user-session (get-user-session current-user)]
                    (let [id-token (.getIdToken user-session)
                          jwt-token (.getJwtToken id-token)
                          token-exp (.getExpiration id-token)
                          id-token-payload (js->clj (.decodePayload id-token))]
                      (when (and user-session id-token jwt-token token-exp)
                        {:current-user current-user
                         :user-session user-session
                         :id-token {:jwt jwt-token
                                    :exp token-exp
                                    :payload id-token-payload}}))))]
    (assoc cofx :session session)))

(rf/reg-cofx ::session session-cofx)

(defn- refresh-session-event-fx
  [{:keys [session] :as cofx} _]
  {:pre [(contains? cofx :session)]}
  (if session
    (.refreshSession (:current-user session)
                     (.getRefreshToken (:user-session session))
                     (fn [err _]
                       (if err
                         (session-error)
                         (rf/dispatch [::set-token-and-schedule-refresh]))))
    {:dispatch-n session-error-events}))

(rf/reg-event-fx
 ::refresh-session
 [(rf/inject-cofx ::user-pool/user-pool)
  (rf/inject-cofx ::session)]
 refresh-session-event-fx)

(defn- now-cofx
  "Adds a cofx with a current timestamp in seconds"
  [cofx]
  (assoc cofx :now (quot (.getTime (js/Date.)) 1000)))

(rf/reg-cofx ::now now-cofx)

(rf/reg-event-fx
 ::schedule-token-refresh
 [(rf/inject-cofx ::now)]
 (fn [{:keys [now]} [_ {:keys [exp]}]]
   (let [token-lifetime (int (- exp now))
         ;; If we refresh the token when it's close to the session
         ;; lifetime, cognito returns a new token with a lifetime
         ;; that is the difference between the current time and the
         ;; session expiration time. Which may be lower than the
         ;; configured token lifetime. As we keep refreshing the token
         ;; the lifetime gets shorter and shorter. But we want the dispatch
         ;; not to be more frequent than one second, hence the `(max)` function.
         half-lifetime (quot token-lifetime 2)
         min-validity token-lifetime]

     {:dispatch-later [{:ms (* 1000 (max 1 half-lifetime))
                        :dispatch [::refresh-session min-validity]}]})))

(defn- set-token-and-schedule-refresh-event-fx
  [{:keys [session]} _]
  {:pre [session]}
  (let [id-token (:id-token session)]
    {:dispatch-n [[::set-id-token id-token]
                  [::schedule-token-refresh id-token]]}))

(rf/reg-event-fx
 ::set-token-and-schedule-refresh
 [(rf/inject-cofx ::user-pool/user-pool)
  (rf/inject-cofx ::session)]
 set-token-and-schedule-refresh-event-fx)
