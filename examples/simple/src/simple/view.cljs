(ns simple.view
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::active-view
 (fn [db _]
   (get db :active-view)))

(rf/reg-event-db
 ::set-active-view
 (fn [db [_ active-view]]
   (assoc db :active-view active-view)))
