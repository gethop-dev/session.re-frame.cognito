[![ci-cd](https://github.com/gethop-dev/session.re-frame.cognito/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/gethop-dev/session.re-frame.cognito/actions/workflows/ci-cd.yml)
[![Clojars Project](https://img.shields.io/clojars/v/dev.gethop/session.re-frame.cognito.svg)](https://clojars.org/dev.gethop/session.re-frame.cognito)
# session-manager.re-frame.cognito

A library that provides [re-frame](https://github.com/day8/re-frame/) events for managing [AWS Cognito](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html) user sessions.

## Installation

[![Clojars Project](https://clojars.org/dev.gethop/session.re-frame.cognito/latest-version.svg)](https://clojars.org/dev.gethop/session.re-frame.cognito)

## Usage

### Configuration
Before using any event provided by the library, the configuration must be initated. Two configuration parameters are required:
- Issuer URL `iss`:  It will have the following structure: `https://cognito-idp.{region}.amazonaws.com/{userPoolId}`
- Client id `client-id`: The id of the app-client that the library will use to authentiate. It's important that the app-client to be used can't have a `client-secret`. Clients with a `secret` are intendend to be used at the backend side.

The static configuration will be stored in the `appdb`, and will be used by the rest of the events when required.
``` clojure
(require '[dev.gethop.session.re-frame.cognito :as session])
(rf/dispatch [::session/set-config {:oidc {:iss "https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_pKbrdhQl8"
                                           :client-id "o2ubh2gb4qbt440jd3543dv8g"}}])
```

### Actions
The library provides a series of `re-frame` events to interact with AWS Cognito.

#### Callback events
In general, all events get an optional map of callbacks events: `on-success-evt` and `on-failure-evt`. As the name states, the first event will be dispatched if the operation succeeds, and the second if it fails.

The `on-failure-evt` will get the reason of the failure as a `keyword` argument. The possible values are the following: `:username-not-provided`, `:invalid-password`, `:invalid-parameter`, `login-attempts-limit-exceeded`,  `disabled-user`, `incorrect-username-or-password`, `username-exists`, `user-not-found`, `code-mistmatch`, `code-expired`, `daily-operations-limit-exceeded`, `account-unverified`, `password-reset-required` and `unknown-error`.
#### Register
**Creating the user:**
``` clojure
(require '[dev.gethop.session.re-frame.cognito.action.register :as session.register])
(rf/dispatch [::session.register/user-register
              {:username "user" :password "pass"}
              {:name "John" :surname "Doe"}
              {:on-success-evt [::register-success]
               :on-failure-evt [::register-failure]}])
```
Arguments:
- credentials: `username` and `password`
- map of [user attributes](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-attributes.html).  Both standard and custom attributes can be used. The attributes must be enabled and configured in the AWS Console for the app-client that it's being used.
- [callback events](#callback-events)

**Verifying the user**
If email/sms code verification is enabled, the user has to be confirmed before logging in. For that, the following event is provided:
``` clojure
(rf/dispatch [::session.register/user-confirm-registration
              verification-code
              {:on-success-evt [::register-confirmation-success]
               :on-failure-evt [::failure]}])
```
 Arguments:
 - `verification-code`. Code received by SMS or email
 - [callback events](#callback-events)

**Resending the verification code**
The library provides an event to resend the verification code to the user.
``` clojure
(rf/dispatch [::session.register/resend-user-verification-code
              {:on-success-evt [::success]
               :on-failure-evt [::failure]}])
```

Note that nor the `user-confirm-registration` event or `resend-user-verification-code` require to send any user infomation. That's because in order for this events to work, first the `session.register/user-register` or `session.login/user-login` events must be dispatched.

#### Login
``` clojure
(rf/dispatch [::session.login/user-login {:username "user" :password "pass"}
                                         {:on-success-evt [::login-success]
                                          :on-failure-evt [::login-failure]}])
```

#### Password change challenge
If the user is in the `force-change-password` state, the login will fail with the `new-password-challenge` error code.  The error means that the user must set a new password. The change can be performed with the following event.
``` clojure
(rf/dispatch [::session.change-password-challenge/user-new-password-challenge
              credentials
              {:on-success-evt [::challenge-success]
               :on-failure-evt [::util/generic-failure]}])
```

#### Logout
``` clojure
(rf/dispatch [::session.logout/user-logout {:on-success-evt [::success]
                                            :on-failure-evt [::failure]}])
```
#### Forgot password
If the user doesn't remember the password, two steps need to be performed:
**Request verification code**
``` clojure
(rf/dispatch [::session.forgot-password/user-request-password-reset
              username
              {:on-success-evt [::request-code-success]
               :on-failure-evt [::util/generic-failure]}])
```
**Set new password**
The verification code wil be sent to the user's email or SMS depending on how the user-pool was configured.
``` clojure
(rf/dispatch [::session.forgot-password/user-reset-password-confirmation
              new-password
              verification-code
              {:on-success-evt [::util/generic-success]
               :on-failure-evt [::util/generic-failure]}])
```
#### Change password
If the user is already logged in, the following event can be used to change the password:

``` clojure
(rf/dispatch [::session.change-password/user-change-password
              {:old-password "old-pass" :new-password "new-pass"}
              {:on-success-evt [::success]
               :on-failure-evt [::failure]}])
```

###  User ID token
The library provides a `re-frame` subscribe to get the details about the user's ID token. The library is responsible of refreshing the token to mantain it always updated.
``` clojure
(:require '[dev.gethop.session.re-frame.cognito.token :as session.token])
(rf/subscribe [::session.token/id-token])
```
Example:
``` json
{
  "jwt": "eyJraWQiOiJyaURBRHlNNnl0SnJNcFh4cTByd0d3ZnJmRVpRd1oyY2tOV1U4Y3UrXC82UT0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkZDZlYzIzYS03YWFjLTQyYTYtOTU4Yi1lMTE3YzkzMWIzZDQiLCJhdWQiOiJvMnViaDJnYjRxYnQ0NDBqZDM1NDNkdjhnIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImV2ZW50X2lkIjoiMWQ5NGY1MGQtYjAzMi00MGYxLThkYTMtMDg1NjllN2I0NjliIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE2NTQ2NzYwNzAsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS13ZXN0LTEuYW1hem9uYXdzLmNvbVwvZXUtd2VzdC0xX3BLYnJkaFFsOCIsImNvZ25pdG86dXNlcm5hbWUiOiJkZDZlYzIzYS03YWFjLTQyYTYtOTU4Yi1lMTE3YzkzMWIzZDQiLCJleHAiOjE2NTQ2Nzk2NzAsImlhdCI6MTY1NDY3NjA3MCwiZW1haWwiOiJ0ZXN0QG1hZ25ldC5jb29wIn0.aRJvc5rIjMECFM44zJt8fbKwGfzrYZiy9zcruE5eZm_sLkwWauNTXshIPDaMlgS9uZ92lYN3nWrDrKJLVPiHqrfJZ5hPbCtjswVlzkzGbGaV_F01D2GNwU6xeV_8XGOt8BxBIWmYVzAycG1UVazQewA2vNV8gyR3H2TqGuAgxkOwMddMiu4ObV1krr2G7qkLzo12jyMGzn4xsZxbbaxdYXw05xoEVZpnO8fTTG8Ygnb5b6Q6H9nByX6rRGweL9CJ2TsKRFrhu5vtZrAucRsSCNlI9_M3Prm5xeo-7bNmAwx0a_qfOpdLDnnbO65FR0Fq845_w-SLaWAuTZKp76nwQA",
  "exp": 1654679670,
  "payload": {
    "sub": "9ee9974d-0e2c-4524-960d-efc580ebc42c",
    "aud": "o2ubh2gb4qbt440jd3543dv8g",
    "email_verified": true,
    "event_id": "1d94f50d-b032-40f1-8da3-08569e7b469b",
    "token_use": "id",
    "auth_time": 1654676070,
    "iss": "https://cognito-idp.eu-west-1.amazonaws.com/eu-west-1_zgbrdgOl3",
    "cognito:username": "9ee9974d-0e2c-4524-960d-efc580ebc42c",
    "exp": 1654679670,
    "iat": 1654676070,
    "email": "test@example.invalid"
  }
}
```
### Sample project

You can find a sample project that covers all the functionality provided by the library in the [examples](examples) directory.

## License

Copyright (c) 2022 HOP Technologies.

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
