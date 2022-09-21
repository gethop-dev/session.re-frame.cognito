(ns simple.view.password-change-challenge
  (:require [dev.gethop.session.re-frame.cognito.action.challenge.change-password :as session.change-password-challenge]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(rf/reg-event-fx
 ::challenge-success
 (fn [_ _]
   {:dispatch [::view/set-active-view :profile]}))

(defn- do-password-change-challenge
  [credentials]
  (rf/dispatch [::session.change-password-challenge/user-new-password-challenge
                credentials
                {:on-success-evt [::challenge-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:div
       [:form.session-form
        {:on-submit
         (fn [e]
           (.preventDefault e)
           (do-password-change-challenge @credentials))}
        [:h2 "Password change required"]
        [:input
         {:type "password"
          :placeholder "new password"
          :value (:password @credentials)
          :on-change (partial util/swap-input! credentials :password)}]
        [:button
         {:type "submit"}
         "Change password"]]])))
