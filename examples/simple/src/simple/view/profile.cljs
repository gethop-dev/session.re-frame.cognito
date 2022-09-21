(ns simple.view.profile
  (:require [dev.gethop.session.re-frame.cognito.action.logout :as session.logout]
            [dev.gethop.session.re-frame.cognito.token :as session.token]
            [re-frame.core :as rf]
            [simple.view :as view]))

(defn- logout-btn []
  [:button
   {:on-click #(rf/dispatch [::session.logout/user-logout
                             {:on-success-evt [::view/set-active-view :login]}])}
   "Logout"])

(defn- change-password-btn []
  [:button
   {:on-click #(rf/dispatch [::view/set-active-view :change-password])}
   "Change password"])

(defn main []
  (let [id-token (rf/subscribe [::session.token/id-token])]
    (fn []
      [:div
       [:h2 "Session"]
       [:pre.session-info
        (.stringify js/JSON (clj->js @id-token) nil 2)]
       [logout-btn]
       [change-password-btn]])))
