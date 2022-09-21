(ns dev.gethop.session.re-frame.cognito.state)

(defonce cognito-user-obj (atom nil))

(defn init-user-object
  [user-pool username]
  (let [user-data #js {:Username username :Pool user-pool}
        user-object (js/AmazonCognitoIdentity.CognitoUser. user-data)]
    (reset! cognito-user-obj user-object)))
