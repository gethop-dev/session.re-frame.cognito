;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns simple.view.forgot-password
  (:require [dev.gethop.session.re-frame.cognito.action.forgot-password :as session.forgot-password]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]
            [simple.view :as view]))

(rf/reg-event-fx
 ::request-code-success
 (fn [_ _]
   {:dispatch [::view/set-active-view :forgot-password-confirmation]}))

(defn- do-request-forgot-password
  [username]
  (rf/dispatch [::session.forgot-password/user-request-password-reset
                username
                {:on-success-evt [::request-code-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:form.session-form
       {:on-submit
        (fn [e]
          (.preventDefault e)
          (do-request-forgot-password (:username @credentials)))}
       [:h2 "Forgot password - request code"]
       [:input
        {:type "email"
         :placeholder "email"
         :value (:username @credentials)
         :on-change (partial util/swap-input! credentials :username)}]
       [:button
        {:type "submit"}
        "Request code"]])))
