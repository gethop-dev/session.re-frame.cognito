;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns simple.view.forgot-password-confirmation
  (:require [dev.gethop.session.re-frame.cognito.action.forgot-password :as session.forgot-password]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(defn- do-reset-password-confirmation
  [{:keys [password verification-code]}]
  (rf/dispatch [::session.forgot-password/user-reset-password-confirmation
                password
                verification-code
                {:on-success-evt [::util/generic-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:form.session-form
       {:on-submit
        (fn [e]
          (.preventDefault e)
          (do-reset-password-confirmation @credentials))}
       [:h2 "Forgot password - confirm password"]
       [:input
        {:value (:verification-code @credentials)
         :placeholder "verification code"
         :on-change (partial util/swap-input! credentials :verification-code)}]
       [:input
        {:type "password"
         :value (:password @credentials)
         :placeholder "password"
         :on-change (partial util/swap-input! credentials :password)}]
       [:button
        {:type "submit"}
        "Reset password"]])))
