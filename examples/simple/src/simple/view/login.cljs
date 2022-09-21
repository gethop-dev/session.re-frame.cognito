(ns simple.view.login
  (:require [dev.gethop.session.re-frame.cognito.action.login :as session.login]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(rf/reg-event-fx
 ::login-success
 (fn [_ _]
   {:dispatch [::view/set-active-view :profile]}))

(rf/reg-event-fx
 ::login-failure
 (fn [_ [_ reason]]
   (case reason
     :account-unverified
     {:dispatch [::view/set-active-view :register-confirmation]}
     :new-password-challenge
     {:dispatch [::view/set-active-view :password-change-challenge]}
     {:dispatch [::util/generic-failure reason]})))

(defn- do-login
  [credentials]
  (rf/dispatch [::session.login/user-login
                credentials
                {:on-success-evt [::login-success]
                 :on-failure-evt [::login-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:div
       [:form.session-form
        {:on-submit
         (fn [e]
           (.preventDefault e)
           (do-login @credentials))}
        [:h2 "Login"]
        [:input
         {:type "email"
          :placeholder "email"
          :value (:username @credentials)
          :on-change (partial util/swap-input! credentials :username)}]
        [:input
         {:type "password"
          :placeholder "password"
          :value (:password @credentials)
          :on-change (partial util/swap-input! credentials :password)}]
        [:button
         {:type "submit"}
         "Login"]]
       [:button
        {:on-click #(rf/dispatch [::view/set-active-view :forgot-password])}
        "Forgot password"]
       [:button
        {:on-click #(rf/dispatch [::view/set-active-view :register])}
        "Register"]])))
