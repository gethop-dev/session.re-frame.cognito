;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.session.re-frame.cognito.util.error
  (:require [clojure.string :as str]
            [goog.object :as g]))

(defn- parse-error
  [error]
  {:code (g/get error "name")
   :message (g/get error "message")})

(defn error->reason
  [error]
  (let [{:keys [code message]} (parse-error error)]
    (case code
      "InvalidParameterException" (cond
                                    (str/includes? message "USERNAME") :username-not-provided
                                    (str/includes? message "password") :invalid-password
                                    :else :invalid-parameter)
      "NotAuthorizedException" (cond
                                 (str/includes? message "exceeded") :login-attempts-limit-exceeded
                                 (str/includes? message "disabled") :disabled-user
                                 :else :incorrect-username-or-password)
      "InvalidPasswordException" :invalid-password
      "UsernameExistsException" :username-exists
      "UserNotFoundException" :user-not-found
      "CodeMismatchException" :code-mismatch
      "ExpiredCodeException" :code-expired
      "LimitExceededException" :daily-operations-limit-exceeded
      "UserNotConfirmedException" :account-unverified
      "PasswordResetRequiredException" :password-reset-required
      :unknown-error)))
