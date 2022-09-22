;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.util
  (:require [dev.gethop.session.re-frame.cognito.util.error :as util.error]
            [re-frame.core :as rf]))

(rf/reg-event-fx
 ::generic-success
 (fn [_ [_ on-success-evt]]
   (if on-success-evt
     {:dispatch on-success-evt}
     {})))

(rf/reg-event-fx
 ::generic-failure
 (fn [_ [_ on-failure-evt error]]
   (let [reason (util.error/error->reason error)]
     (if on-failure-evt
       {:dispatch (conj on-failure-evt reason)}
       {}))))
