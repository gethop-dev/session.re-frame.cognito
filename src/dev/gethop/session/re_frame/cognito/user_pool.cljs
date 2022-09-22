;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.user-pool
  (:require [cljsjs.amazon-cognito-identity-js]
            [clojure.string :as str]
            [dev.gethop.session.re-frame.cognito :as session.cognito]
            [re-frame.core :as rf]))

(defn get-user-pool
  [db]
  (let [user-pool-id (last (str/split (get-in db [::session.cognito/config :oidc :iss]) #"/"))
        client-id (get-in db [::session.cognito/config :oidc :client-id])]
    (js/AmazonCognitoIdentity.CognitoUserPool.
     #js {:UserPoolId user-pool-id :ClientId client-id})))

(defn- user-pool-cofx
  [{:keys [db] :as cofx} _]
  {:pre [(::session.cognito/config db)]
   :post [(contains? % :user-pool)]}
  (rf/console :log "user-pool cofx" (clj->js cofx))
  (assoc cofx :user-pool (get-user-pool db)))

(rf/reg-cofx ::user-pool user-pool-cofx)
