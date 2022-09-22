;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns simple.util
  (:require [goog.object :as g]
            [re-frame.core :as rf]))

(defn swap-input!
  [atom key event]
  (let [value (g/getValueByKeys event "target" "value")]
    (swap! atom assoc key value)))

(rf/reg-fx
 :alert
 (fn [msg]
   (js/alert msg)))

(rf/reg-event-fx
 ::generic-success
 (fn [_ _]
   {:alert "Success!"}))

(rf/reg-event-fx
 ::generic-failure
 (fn [_ [_ reason]]
   {:alert (str "Failure! reason: " reason)}))
