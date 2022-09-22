;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.state)

(defonce cognito-user-obj (atom nil))

(defn init-user-object
  [user-pool username]
  (let [user-data #js {:Username username :Pool user-pool}
        user-object (js/AmazonCognitoIdentity.CognitoUser. user-data)]
    (reset! cognito-user-obj user-object)))
