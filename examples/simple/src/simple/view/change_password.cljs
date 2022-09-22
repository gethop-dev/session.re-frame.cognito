;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns simple.view.change-password
  (:require [dev.gethop.session.re-frame.cognito.action.change-password :as session.change-password]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [simple.util :as util]))

(defn- do-change-password
  [credentials]
  (rf/dispatch [::session.change-password/user-change-password
                credentials
                {:on-success-evt [::util/generic-success]
                 :on-failure-evt [::util/generic-failure]}]))

(defn main []
  (let [credentials (r/atom {})]
    (fn []
      [:form.session-form
       {:on-submit
        (fn [e]
          (.preventDefault e)
          (do-change-password @credentials))}
       [:h2 "Change password"]
       [:input
        {:type "password"
         :placeholder "old password"
         :value (:old-password @credentials)
         :on-change (partial util/swap-input! credentials :old-password)}]
       [:input
        {:type "password"
         :placeholder "new password"
         :value (:new-password @credentials)
         :on-change (partial util/swap-input! credentials :new-password)}]
       [:button
        {:type "submit"}
        "Change password"]])))
