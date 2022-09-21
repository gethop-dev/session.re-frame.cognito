(ns simple.view.register
  (:require [dev.gethop.session.re-frame.cognito.action.register :as session.register]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(rf/reg-event-fx
 ::register-success
 (fn [_ _]
   {:dispatch [::view/set-active-view :register-confirmation]}))

(defn- do-register
  [credentials]
  (rf/dispatch [::session.register/user-register
                credentials
                {}
                {:on-success-evt [::register-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:form.session-form
       {:on-submit
        (fn [e]
          (.preventDefault e)
          (do-register @credentials))}
       [:h2 "Register"]
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
        "Register"]])))
