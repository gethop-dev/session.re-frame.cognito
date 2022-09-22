;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.action.forgot-password
  (:require [cljsjs.amazon-cognito-identity-js]
            [dev.gethop.session.re-frame.cognito.state :as state]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [dev.gethop.session.re-frame.cognito.util :as util]
            [re-frame.core :as rf]))

(rf/reg-fx
 ::do-user-request-password-reset
 (fn [{:keys [user-pool username]
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (state/init-user-object user-pool username)
   (.forgotPassword
    @state/cognito-user-obj
    #js {:onSuccess
         #(rf/dispatch [::util/generic-success on-success-evt])
         :onFailure
         #(rf/dispatch [::util/generic-failure on-failure-evt %])})))

(rf/reg-event-fx
 ::user-request-password-reset
 [(rf/inject-cofx ::user-pool/user-pool)]
 (fn [{:keys [user-pool]} [_ username opts]]
   {::do-user-request-password-reset {:user-pool user-pool
                                      :username username
                                      :opts opts}}))

(rf/reg-fx
 ::do-user-reset-password-confirmation
 (fn [{:keys [password verification-code]
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (if-not @state/cognito-user-obj
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session)))
     (.confirmPassword
      @state/cognito-user-obj
      verification-code
      password
      #js {:onSuccess
           #(rf/dispatch [::util/generic-success on-success-evt])
           :onFailure
           #(rf/dispatch [::util/generic-failure on-failure-evt %])}))))

(rf/reg-event-fx
 ::user-reset-password-confirmation
 (fn [_ [_ password verification-code opts]]
   {::do-user-reset-password-confirmation {:password password
                                           :verification-code verification-code
                                           :opts opts}}))
