# システムアーキテクチャ設計

本ドキュメントは、Issue [#1 新規サービスの立ち上げ](https://github.com/yunoha/ai-powered-dev-sandbox/issues/1) に対応するシステムアーキテクチャを定義するものである。

## システム全体像

```
┌─────────────────────────────────────────────────────────┐
│                   Google Cloud                          │
│                                                         │
│  ┌───────────────────┐      ┌────────────────────────┐  │
│  │   Cloud Run        │      │   Cloud Run             │  │
│  │   (Frontend)       │      │   (Backend)             │  │
│  │                    │ HTTP │                          │  │
│  │   Next.js/React   │─────▶│   Spring Boot (Kotlin)  │  │
│  │   (BFF)           │      │                          │  │
│  └────────┬──────────┘      └────────────────────────┘  │
│           │                                              │
└───────────┼──────────────────────────────────────────────┘
            │ HTTPS
            │
    ┌───────┴───────┐
    │   ユーザー     │
    │  (ブラウザ)    │
    └───────────────┘
```

## コンポーネント構成

### フロントエンド (Next.js/React BFF)

- **技術**: Next.js + React
- **役割**: BFF (Backend for Frontend) として機能し、サーバーサイドレンダリング (SSR) でバックエンド API から取得したデータを HTML としてユーザーに提供する
- **ランタイム**: Node.js
- **ソースコード配置**: `product/frontend/`

#### リクエストフロー

1. ユーザーがブラウザで `/` にアクセス
2. Next.js サーバーがリクエストを受け取り、サーバーサイドでバックエンド API (`GET /api/greeting`) を呼び出す
3. バックエンドから受け取ったレスポンスをもとに HTML をレンダリングしてクライアントに返す

### バックエンド (Spring Boot)

- **技術**: Spring Boot + Kotlin
- **役割**: ビジネスロジックを提供する REST API サーバー
- **ランタイム**: JVM (Java 21)
- **ソースコード配置**: `product/backend/`

#### 提供 API

- `GET /api/greeting` — 挨拶メッセージを返す (詳細は[製品仕様書](../spec/product-spec.md)を参照)

### API インターフェイス

- **方式**: REST (HTTP/JSON)
- **スキーマ定義**: OpenAPI 3.0
- **スキーマ管理**: OpenAPI 仕様ファイル (`product/api-schema/openapi.yaml`) をソースオブトゥルースとして管理する
- **コード生成**: OpenAPI スキーマからクライアントコード (フロントエンド用) およびサーバースタブ (バックエンド用) を生成する方針とする
  - フロントエンド側: openapi-generator 等を利用して TypeScript クライアントを生成
  - バックエンド側: openapi-generator 等を利用して Kotlin/Spring のインターフェイスを生成

## インフラストラクチャ

### クラウドプロバイダー

**Google Cloud** を利用する。

### 利用サービス

| サービス | 用途 |
|---------|------|
| Cloud Run | フロントエンド・バックエンドのコンテナ実行環境 |
| Artifact Registry | コンテナイメージの管理 |

> 注: 本フェーズではデータベースやキャッシュ等の外部ストレージは不要。今後の機能拡張時に追加する。

### インフラ定義

- **ツール**: Terraform
- **コード配置**: `product/infrastructure/`
- **管理対象**:
  - Cloud Run サービス (フロントエンド)
  - Cloud Run サービス (バックエンド)
  - Artifact Registry リポジトリ
  - 必要な IAM 設定

### ネットワーク構成

- フロントエンド (Cloud Run): インターネットからのアクセスを許可 (HTTPS)
- バックエンド (Cloud Run): フロントエンドからの内部通信のみ許可
  - Cloud Run のサービス間認証を利用してフロントエンドからのみアクセスできるようにする

## 開発環境

### Dev Container

生成 AI が制限されたコンテキストの中で作業できるように、Dev Container を用意する。

- **定義ファイル配置**: `.devcontainer/`

#### インストールするツール

| ツール | 用途 |
|--------|------|
| Node.js (LTS) | Next.js フロントエンドの開発・ビルド |
| JDK 21 | Spring Boot バックエンドの開発・ビルド |
| Terraform | インフラ定義の管理・適用 |
| Google Cloud CLI (gcloud) | Google Cloud リソースの操作 |
| Docker CLI | コンテナイメージのビルド・テスト |
| Git | バージョン管理 |

#### VS Code 拡張機能

Dev Container には以下の VS Code 拡張機能を含める:

- GitHub Copilot
- Kotlin Language
- Spring Boot Extension Pack (必要に応じて)
- ESLint / Prettier (フロントエンド用)

## ディレクトリ構成

```
ai-powered-dev-sandbox/
├── .devcontainer/           # Dev Container 定義
│   └── devcontainer.json
├── docs/
│   ├── spec/                # 製品仕様
│   └── architecture/        # アーキテクチャ設計
├── product/
│   ├── frontend/            # Next.js フロントエンド
│   ├── backend/             # Spring Boot バックエンド
│   ├── api-schema/          # OpenAPI スキーマ定義
│   │   └── openapi.yaml
│   └── infrastructure/      # Terraform 定義
│       └── main.tf
├── tests/                   # E2E テスト
└── README.md
```

## 技術選定の根拠

| 選定事項 | 選定内容 | 根拠 |
|---------|---------|------|
| フロントエンドフレームワーク | Next.js/React | Issue の技術要求に従う。SSR 対応で BFF パターンに適合 |
| バックエンドフレームワーク | Spring Boot | Issue の技術要求に従う。エンタープライズ向け Web API の実績豊富 |
| バックエンド言語 | Kotlin | モダンで簡潔な記述が可能。Spring Boot が公式サポート |
| API 方式 | OpenAPI (REST) | スキーマベースのインターフェイス設計が可能。Spring Boot / Next.js 双方で成熟したツールチェーンが存在 |
| クラウドプロバイダー | Google Cloud | Issue の技術要求に合致。Cloud Run によるコンテナベースのサーバーレス実行が可能 |
| IaC | Terraform | Issue の技術要求に従う。マルチクラウド対応で宣言的なインフラ管理が可能 |
