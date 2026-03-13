# 開発環境セットアップ

このドキュメントは、`ai-powered-dev-sandbox` のローカル開発環境を構築し、フロントエンド・バックエンド・E2E テストを実行するための手順をまとめたものである。

## 推奨する開発方法

現在のリポジトリは **Dev Container を用いた開発** を前提としている。

理由は以下の通り。

- フロントエンドでは Node.js と npm が必要
- バックエンドでは Java 25 と Gradle が必要
- 今後のインフラ実装では Terraform も必要
- 依存ツールのバージョン差異を減らせる

## ディレクトリ構成

主要なディレクトリは以下の通り。

- `product/frontend/`: Next.js フロントエンド
- `product/backend/`: Spring Boot / Kotlin バックエンド
- `tests/`: Playwright による E2E テスト
- `docs/spec/`: 製品仕様と OpenAPI スキーマ
- `docs/architecture/`: アーキテクチャ設計
- `.devcontainer/`: Dev Container 定義

## Dev Container の前提条件

Dev Container を利用する場合は、手元のマシンに以下を用意する。

- Visual Studio Code
- Dev Containers 拡張機能
- Docker Desktop など、Dev Container を起動できるコンテナ実行環境

## Dev Container での起動手順

1. リポジトリを Visual Studio Code で開く
2. `Dev Containers: Reopen in Container` を実行する
3. コンテナ作成完了後、依存関係がインストールされていることを確認する

現状の `postCreateCommand` では以下が実行される。

- `product/frontend/` で `npm install`
- `tests/` で `npm install`

## 初回セットアップ後に追加で必要な作業

Playwright のブラウザ本体も `postCreateCommand` で導入されるため、コンテナ作成直後から E2E テストを実行できる。

バックエンドは Gradle Wrapper をまだ追加していないため、Dev Container 内で利用可能な `gradle` コマンドをそのまま使う前提である。

## 実行コマンド

### バックエンドの起動

```bash
cd product/backend
gradle bootRun
```

起動後、以下で API を確認できる。

```bash
curl http://127.0.0.1:8080/api/greeting
```

期待されるレスポンス:

```json
{"message":"Hello world!"}
```

### バックエンドのテスト

```bash
cd product/backend
gradle test
```

### フロントエンドの起動

別ターミナルで以下を実行する。

```bash
cd product/frontend
BACKEND_BASE_URL=http://127.0.0.1:8080 npm run dev
```

ブラウザで `http://127.0.0.1:3000` を開くと、トップページに `Hello world!` が表示される想定である。

### E2E テストの実行

```bash
cd tests
npm run test:e2e
```

`tests/playwright.config.ts` では、以下を自動起動する設定になっている。

- バックエンド: `gradle bootRun`
- フロントエンド: `npm run dev`

## 環境変数

現時点でアプリケーション実行に関係する主な環境変数は以下の通り。

- `BACKEND_BASE_URL`: フロントエンドが接続するバックエンドの URL
  - 省略時の既定値は `http://127.0.0.1:8080`
- `PORT`: Spring Boot の待受ポート
  - 省略時の既定値は `8080`

## 既知の注意点

- ローカルホストマシンに Node.js / Java / Gradle が入っていなくても、Dev Container 内であれば作業できる
- Gradle Wrapper (`gradlew`) はまだ存在しないため、Gradle が使える環境での実行が前提
- インフラ定義 (`product/infrastructure/`) はまだ未実装

## 今後の改善候補

- Gradle Wrapper の追加
- フロントエンド / バックエンドの起動をまとめるタスクランナーの追加
- CI 上での自動テスト実行
