# LoginTester プロジェクト

このプロジェクトは、Spring Bootを使ったセッション管理とCSRF対策を実装したログイン処理の動作確認用サンプルです。

* セッションを使ったユーザー認証
* CSRFトークンによるフォームの安全性確保



\*\*ディレクトリ構造\*\*

src/main/java/com/example/sns

/config

\- GlobalCsrfAdvice.java

\- SecurityConfig.java

/controller

\- LoginRegisterController.java

/dto

\- LoginRequest.java

\- RegisterRequest.java

/entity

\- Users.java

/helper

\- LoginUserHelper.java

/repository

\- UsersRepository.java(interface)

/security

\- AuthenticationHelper.java

\- CustomUserDetailsService.java

\- UsersDetails.java

/service

\- AuthenticationService.java

\-  UsersService.java

/util

\- LoginIdGenerator.java

/validator

\- UserRegistrationValidator.java

LoginTesterApplication(mainメソッド)

