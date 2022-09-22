;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.action.logout
  (:require [cljsjs.amazon-cognito-identity-js]
            [dev.gethop.session.re-frame.cognito.token :as token]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 ::on-logout-success
 (fn [_ [_ on-success-evt]]
   {:dispatch-n [[::token/remove-token]
                 (when on-success-evt
                   on-success-evt)]}))

(rf/reg-fx
 ::do-user-logout
 (fn [{:keys [session]
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (if-let [user (:current-user session)]
     (do
       (.signOut user)
       (rf/dispatch [::on-logout-success on-success-evt]))
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session))))))

(rf/reg-event-fx
 ::user-logout
 [(rf/inject-cofx ::user-pool/user-pool)
  (rf/inject-cofx ::token/session)]
 (fn [{:keys [session]} [_ opts]]
   {::do-user-logout {:session session
                      :opts opts}}))
