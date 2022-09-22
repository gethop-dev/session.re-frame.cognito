;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.action.register
  (:require [dev.gethop.session.re-frame.cognito.state :as state]
            [dev.gethop.session.re-frame.cognito.user-pool :as user-pool]
            [dev.gethop.session.re-frame.cognito.util :as util]
            [re-frame.core :as rf]))

(def ^:const standard-attributes [:address :birthdate :email :family_name
                                  :gender :given_name :locale :middle_name
                                  :name :nickname :phone_number :picture
                                  :preferred_username :profile :updated_at
                                  :website :zoneinfo])

(defn- new-cognito-user-attribute
  [key value]
  (let [attribute-name (if (some #{key} standard-attributes)
                         (name key)
                         (str "custom:" (name key)))
        attribute (clj->js {:Name attribute-name :Value value})]
    (new js/AmazonCognitoIdentity.CognitoUserAttribute attribute)))

(defn- attributes->cognito-attributes
  [attributes]
  (->> attributes
       (mapv (fn [[k v]] (new-cognito-user-attribute k v)))
       (clj->js)))

(rf/reg-fx
 ::do-user-register-confirmation
 (fn [{:keys [verification-code]
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (if-not @state/cognito-user-obj
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session)))
     (.confirmRegistration
      @state/cognito-user-obj
      verification-code
      true
      (fn [error _result]
        (if error
          (rf/dispatch [::util/generic-failure on-failure-evt error])
          (rf/dispatch [::util/generic-success on-success-evt])))))))

(rf/reg-event-fx
 ::user-confirm-registration
 (fn [_ [_ verification-code opts]]
   {::do-user-register-confirmation {:verification-code verification-code
                                     :opts opts}}))

(rf/reg-fx
 ::do-resend-verification-code
 (fn [{{:keys [on-success-evt on-failure-evt]} :opts}]
   (if-not @state/cognito-user-obj
     (when on-failure-evt
       (rf/dispatch (conj on-failure-evt :no-session)))
     (.resendConfirmationCode
      @state/cognito-user-obj
      (fn [error _result]
        (if error
          (rf/dispatch [::util/generic-failure on-failure-evt error])
          (rf/dispatch [::util/generic-success on-success-evt])))))))

(rf/reg-event-fx
 ::resend-user-verification-code
 (fn [_ [_ opts]]
   {::do-resend-verification-code {:opts opts}}))

(rf/reg-fx
 ::do-user-register
 (fn [{:keys [user-pool attributes]
       {:keys [username password]} :credentials
       {:keys [on-success-evt on-failure-evt]} :opts}]
   (let [attributes (attributes->cognito-attributes attributes)]
     (.signUp
      user-pool
      username
      password
      attributes
      nil
      (fn [error _result]
        (if error
          (rf/dispatch [::util/generic-failure on-failure-evt error])
          (do
            (state/init-user-object user-pool username)
            (rf/dispatch [::util/generic-success on-success-evt]))))
      nil))))

(rf/reg-event-fx
 ::user-register
 [(rf/inject-cofx ::user-pool/user-pool)]
 (fn [{:keys [user-pool]} [_ credentials attributes opts]]
   {::do-user-register {:user-pool user-pool
                        :credentials credentials
                        :attributes attributes
                        :opts opts}}))
