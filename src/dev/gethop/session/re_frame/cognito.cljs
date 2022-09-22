;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito
  (:require [cljsjs.amazon-cognito-identity-js]
            [re-frame.core :as rf]))

(rf/reg-event-db
 ::set-config
 (fn [db [_ config]]
   (assoc db ::config config)))
