(ns ^:figwheel-hooks simple.core
  (:require [dev.gethop.session.re-frame.cognito :as session]
            [dev.gethop.session.re-frame.cognito.token :as session.token]
            [re-frame.core :as rf]
            [reagent.dom :as rd]
            [simple.view :as view]
            [simple.view.change-password :as view.change-password]
            [simple.view.forgot-password :as view.forgot-password]
            [simple.view.forgot-password-confirmation :as view.forgot-password-confirmation]
            [simple.view.login :as view.login]
            [simple.view.password-change-challenge :as view.password-change-challenge]
            [simple.view.profile :as view.profile]
            [simple.view.register :as view.register]
            [simple.view.register-confirmation :as view.register-confirmation]))

(defn- main []
  (let [active-view (rf/subscribe [::view/active-view])
        logged-in? (rf/subscribe [::session.token/id-token])]
    (fn []
      [:div.session
       [:h1
        {:on-click
         (fn [_]
           (if @logged-in?
             (rf/dispatch [::view/set-active-view :profile])
             (rf/dispatch [::view/set-active-view :login])))}
        "hop.session.re-frame.cognito sample project"]
       (case (or @active-view :login)
         :login
         [view.login/main]

         :password-change-challenge
         [view.password-change-challenge/main]

         :profile
         [view.profile/main]

         :register
         [view.register/main]
         :register-confirmation
         [view.register-confirmation/main]

         :change-password
         [view.change-password/main]

         :forgot-password
         [view.forgot-password/main]
         :forgot-password-confirmation
         [view.forgot-password-confirmation/main])])))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (rd/render [main] (.getElementById js/document "app")))

(defn ^:after-load re-render []
  (mount-root))

(defn ^:export init []
  (rf/dispatch-sync [::session/set-config {:oidc {:iss "https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_pKbrdhQl8"
                                                  :client-id "o2ubh2gb4qbt440jd3543dv8g"}}])
  (rf/dispatch [::session.token/set-token-and-schedule-refresh])
  (mount-root))
