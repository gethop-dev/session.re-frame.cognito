;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.action.challenge.change-password
  (:require [dev.gethop.session.re-frame.cognito.state :as state]
            [dev.gethop.session.re-frame.cognito.token :as token]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [dev.gethop.session.re-frame.cognito.util :as util]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 ::on-success
 (fn [_ [_ on-success-evt]]
   {:dispatch-n [[::token/set-token-and-schedule-refresh]
                 (when on-success-evt
                   on-success-evt)]}))

(rf/reg-fx
 ::do-complete-new-password-challenge
 (fn [{{:keys [password]} :credentials
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (if-not @state/cognito-user-obj
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session)))
     (.completeNewPasswordChallenge
      @state/cognito-user-obj
      password
      #js []
      #js {:onSuccess
           #(rf/dispatch [::on-success on-success-evt])
           :onFailure
           #(rf/dispatch [::util/generic-failure on-failure-evt %])}))))

(rf/reg-event-fx
 ::user-new-password-challenge
 [(rf/inject-cofx ::user-pool/user-pool)]
 (fn [{:keys [user-pool]} [_ credentials opts]]
   {::do-complete-new-password-challenge {:user-pool user-pool
                                          :credentials credentials
                                          :opts opts}}))
