(ns dev.gethop.session.re-frame.cognito
  (:require [cljsjs.amazon-cognito-identity-js]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::set-config
 (fn [db [_ config]]
   (assoc db ::config config)))
