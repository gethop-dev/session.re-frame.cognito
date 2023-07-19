# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [UNRELEASED]

## 0.1.1-alpha - 2023-07-19
### Changed
- The id-token 'payload' provided by the session coeffect is now a
  CLJ data structure [#1].
### Fixed
- The internal implementation of obtaining the id-token's 'payload'
  was changed to avoid breaking when using advanced CLJS compilation.

## 0.1.0-alpha - 2022-05-03
### Added
- Initial commit

[UNRELEASED]:  https://github.com/gethop-dev/session.re-frame.cognito/compare/v0.1.1-alpha...HEAD
[0.1.1]:  https://github.com/gethop-dev/session.re-frame.cognito/compare/v0.1.0-alpha...v0.1.1-alpha
[0.1.0]: https://github.com/gethop-dev/session.re-frame.cognito/releases/tag/v0.1.0-alpha
=======
