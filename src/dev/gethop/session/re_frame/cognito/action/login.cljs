;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.action.login
  (:require [cljsjs.amazon-cognito-identity-js]
            [dev.gethop.session.re-frame.cognito.state :as state]
            [dev.gethop.session.re-frame.cognito.token :as token]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [dev.gethop.session.re-frame.cognito.util :as util]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 ::on-login-success
 (fn [_ [_ on-success-evt]]
   {:dispatch-n [[::token/set-token-and-schedule-refresh]
                 (when on-success-evt
                   on-success-evt)]}))

(rf/reg-fx
 ::do-user-login
 (fn [{:keys [user-pool]
       {:keys [username password]} :credentials
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (let [auth-details (js/AmazonCognitoIdentity.AuthenticationDetails.
                       #js {:Username username :Password password})]
     (state/init-user-object user-pool username)
     (.authenticateUser
      @state/cognito-user-obj
      auth-details
      #js {:onSuccess
           #(rf/dispatch [::on-login-success on-success-evt])
           :onFailure
           #(rf/dispatch [::util/generic-failure on-failure-evt %])
           :newPasswordRequired
           #(when on-failure-evt
              (rf/dispatch (conj on-failure-evt :new-password-challenge)))}))))

(rf/reg-event-fx
 ::user-login
 [(rf/inject-cofx ::user-pool/user-pool)]
 (fn [{:keys [user-pool]} [_ credentials opts]]
   {::do-user-login {:user-pool user-pool
                     :credentials credentials
                     :opts opts}}))
