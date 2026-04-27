# SMTP 設定チェックリスト

## 目的

このプロジェクトのメール送信は実装自体は存在しますが、SMTP の環境変数が空だと送信できません。
このドキュメントは、`demo/compose.yaml` で使う最小設定を整理したものです。

## 必須環境変数

`demo/.env` または Docker Compose 実行環境で、以下を設定してください。

- `APP_MAIL_ENABLED=true`
- `SPRING_MAIL_USERNAME=<gmail address>`
- `SPRING_MAIL_PASSWORD=<gmail app password>`
- `APP_FRONTEND_BASE_URL=http://localhost:5174`

## 設定手順

1. `demo/.env.example` を `demo/.env` にコピーする
2. `SPRING_MAIL_USERNAME` に送信用 Gmail アドレスを入れる
3. `SPRING_MAIL_PASSWORD` に通常のログインパスワードではなく **Gmail App Password** を入れる
4. Frontend の公開 URL が `http://localhost:5174` であることを確認する
5. `docker compose up -d` を再実行する

## 確認ポイント

- 登録時の認証メール送信
- パスワードリセット要求時のメール送信
- メール内リンクが現在の Frontend URL（`5174`）を指していること

## 補足

- `APP_MAIL_ENABLED=true` だけでは送信できません
- `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` が空だと SMTP 認証に失敗します
- 必要なら `spring.mail.debug=true` を追加して SMTP 接続ログを確認してください
