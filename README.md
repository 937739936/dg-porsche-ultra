# 基于quarkus搭建的脚手架


- 目录结构
```
dg-porsche-ultra/
├── dg-porsche-business/                                             // 业务模块
│   ├── demo/                                                        // demo模块
├── dg-porsche-framework/                                            // 框架
│   ├── porsche-framework-common/                                    // 通用模块
│   ├── porsche-framework-dependencies/                              // 依赖包管理
│   ├── porsche-framework-extension/                                 // 相关扩展
│   │   ├── porsche-framework-extension-web/                         // web模块
│   │   ├── porsche-framework-extension-json/                        // 序列化模块
│   │   ├── porsche-framework-extension-excel/                       // excel模块
│   │   ├── porsche-framework-extension-mail/                        // 邮件模块
│   │   ├── porsche-framework-extension-mysql/                       // 数据库模块
│   │   ├── porsche-framework-extension-redis/                       // 缓存服务模块
│   │   ├── porsche-framework-extension-httpclient/                  // httpClient模块
│   │   ├── porsche-framework-extension-job/                         // 定时任务模块
├── README.md                                                        // 框架说明文件
└── pom.xml                                                         

```

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw clean package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

