;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

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
