(ns dev.gethop.session.re-frame.cognito.action.change-password
  (:require [cljsjs.amazon-cognito-identity-js]
            [dev.gethop.session.re-frame.cognito.token :as token]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [dev.gethop.session.re-frame.cognito.util :as util]
            [re-frame.core :as rf]))

(rf/reg-fx
 ::do-change-password
 (fn [{:keys [session]
       {:keys [old-password new-password]} :credentials
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (if-let [current-user (:current-user session)]
     (.changePassword
      current-user
      old-password
      new-password
      (fn [error _]
        (if error
          (rf/dispatch [::util/generic-failure on-failure-evt error])
          (rf/dispatch [::util/generic-success on-success-evt]))))
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session))))))

(rf/reg-event-fx
 ::user-change-password
 [(rf/inject-cofx ::user-pool/user-pool)
  (rf/inject-cofx ::token/session)]
 (fn [{:keys [user-pool session]} [_ credentials opts]]
   {::do-change-password {:user-pool user-pool
                          :session session
                          :credentials credentials
                          :opts opts}}))
