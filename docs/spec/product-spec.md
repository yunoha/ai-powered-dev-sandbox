# 製品仕様書

本ドキュメントは、Issue [#1 新規サービスの立ち上げ](https://github.com/yunoha/ai-powered-dev-sandbox/issues/1) に対応する製品仕様を定義するものである。

## 概要

「Hello world!」と表示されるトップページを持つ Web アプリケーションを新規に立ち上げる。
本フェーズでは最小限の機能のみ実装し、今後の機能拡張の基盤を構築することを目的とする。

## ユーザー向け機能仕様

### トップページ

- **URL パス**: `/`
- **HTTP メソッド**: GET
- **振る舞い**: ブラウザでアクセスすると、ページ上に「Hello world!」というテキストが表示される
- **レスポンス形式**: HTML

#### 画面要素

| 要素 | 内容 |
|------|------|
| 表示テキスト | `Hello world!` |

> 注: 今後変更する部分であるため、レイアウトやスタイリング等の細かいデザインについてはこの段階では規定しない。

## バックエンド API 仕様

フロントエンド (Next.js BFF) とバックエンド (Spring Boot) 間の通信は **OpenAPI (REST)** に基づくスキーマベースのインターフェイスで行う。

### API エンドポイント一覧

#### GET /api/greeting

挨拶メッセージを取得する。

- **リクエスト**: パラメータなし
- **レスポンス**:
  - ステータスコード: `200 OK`
  - Content-Type: `application/json`
  - ボディ:
    ```json
    {
      "message": "Hello world!"
    }
    ```

### OpenAPI スキーマ

```yaml
openapi: 3.0.3
info:
  title: AI Powered Dev Sandbox API
  version: 0.1.0
  description: Web アプリケーションのバックエンド API
paths:
  /api/greeting:
    get:
      summary: 挨拶メッセージの取得
      operationId: getGreeting
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/GreetingResponse'
components:
  schemas:
    GreetingResponse:
      type: object
      required:
        - message
      properties:
        message:
          type: string
          description: 挨拶メッセージ
          example: "Hello world!"
```

## データモデル

本フェーズでは永続化が必要なデータモデルは存在しない。
バックエンドは固定の挨拶メッセージを返すのみである。

### 概念モデル

| エンティティ | 属性 | 型 | 説明 |
|-------------|------|------|------|
| GreetingResponse | message | string | 挨拶メッセージ |

## 外部との入出力・副作用

### 入力

| 入力元 | 内容 |
|--------|------|
| ユーザー (ブラウザ) | トップページへの HTTP GET リクエスト (`/`) |

### 出力

| 出力先 | 内容 |
|--------|------|
| ユーザー (ブラウザ) | 「Hello world!」を含む HTML ページ |

### 副作用

本フェーズでは副作用を伴う処理は存在しない (データの書き込み、外部サービスへの通知等は行わない)。

## 非機能要件

本フェーズでは最小限の立ち上げが目的であるため、詳細なパフォーマンス要件やスケーラビリティ要件は規定しない。
ただし、以下の点は考慮する:

- アプリケーションはコンテナ化され、クラウド環境にデプロイ可能であること
- 開発者が Dev Container を使用してローカルで開発・動作確認できること
