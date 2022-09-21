(ns simple.view.register-confirmation
  (:require [dev.gethop.session.re-frame.cognito.action.register :as session.register]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(rf/reg-event-fx
 ::register-confirmation-success
 (fn [_ _]
   {:alert "Registration success!"
    :dispatch [::view/set-active-view :login]}))

(defn- do-register-confirmation
  [verification-code]
  (rf/dispatch [::session.register/user-confirm-registration
                verification-code
                {:on-success-evt [::register-confirmation-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:div
       [:form.session-form
        {:on-submit
         (fn [e]
           (.preventDefault e)
           (do-register-confirmation (:verification-code @credentials)))}
        [:h2 "Register - confirmation"]
        [:input
         {:type "number"
          :placeholder "verification code"
          :value (:verification-code @credentials)
          :on-change (partial util/swap-input! credentials :verification-code)}]
        [:button
         {:type "submit"}
         "Confirm registration"]]
       [:button
        {:on-click #(rf/dispatch [::session.register/resend-user-verification-code])}
        "Resend confirmation code"]])))
