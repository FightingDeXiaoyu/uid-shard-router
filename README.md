# uid-shard-router

[![CI](https://github.com/FightingDeXiaoyu/uid-shard-router/actions/workflows/ci.yml/badge.svg)](https://github.com/FightingDeXiaoyu/uid-shard-router/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)

`uid-shard-router` is a small zero-dependency Java library and CLI for deterministic user-id based table routing.

It helps backend teams answer questions like:

- Which physical table should user `10002345` land in?
- How do we generate `user_order_031` consistently across services?
- How do we share one routing rule between online traffic and offline repair scripts?

## Why this project exists

Sharding rules often end up duplicated in service code, SQL scripts, migration jobs, and ad-hoc maintenance tools. That duplication creates routing drift and expensive mistakes.

This project packages the common logic into one tiny reusable component:

- deterministic routing from `uid` to shard index
- configurable suffix width and naming style
- zero external dependencies
- tiny CLI for shell scripts and support workflows
- self-test suite that can run with plain `javac` and `java`

## Features

- Route by `long` user id
- Support `MODULO` and `MIXED_MODULO` hash strategies
- Build final table names like `user_profile_007`
- Support single uid, comma-separated uid lists, and uid files in the CLI
- Generate route metadata for automation or logging
- Run as either embedded library or standalone CLI

## Quick start

### Library

```java
ShardConfig config = ShardConfig.builder()
        .logicTable("user_order")
        .shardCount(64)
        .suffixWidth(3)
        .hashStrategy(HashStrategy.MIXED_MODULO)
        .build();

UidShardRouter router = new UidShardRouter(config);
ShardRoute route = router.route(10002345L);

System.out.println(route.getPhysicalTableName());
// user_order_031
```

### CLI

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName })
java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli --table user_order --uid 10002345 --shards 64 --width 3 --strategy MIXED_MODULO
```

Example output:

```text
uid=10002345
hash=1992740095
shardIndex=63
physicalTable=user_order_063
```

### CLI batch mode

You can also route multiple uids in one execution.

Comma-separated list:

```powershell
java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli --table user_order --uid-list 10002345,10002346,10002347 --shards 64 --width 3 --strategy MIXED_MODULO
```

UID file:

```text
10002345
10002346
10002347
```

```powershell
java -cp out io.github.fightingdexiaoyu.uidshardrouter.cli.ShardCli --table user_order --uid-file uids.txt --shards 64 --width 3 --strategy MIXED_MODULO
```

Batch output example:

```text
uid=10002345
hash=1992740095
shardIndex=63
physicalTable=user_order_063

uid=10002346
hash=61991608
shardIndex=56
physicalTable=user_order_056

uid=10002347
hash=1536963592
shardIndex=8
physicalTable=user_order_008
```

## API overview

- `ShardConfig`: immutable routing configuration
- `UidShardRouter`: core router implementation
- `ShardRoute`: route result with metadata
- `HashStrategy`: pluggable hash behavior enum
- `ShardCli`: simple command-line entry point

## Project structure

```text
src/main/java
src/test/java
examples/basic-usage
```

## Example program

See [examples/basic-usage/BasicUsageExample.java](./examples/basic-usage/BasicUsageExample.java) for a minimal embedded usage example.

Compile and run it together with the library:

```powershell
$main = Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName }
$example = Get-ChildItem -Recurse -Filter *.java examples\basic-usage | ForEach-Object { $_.FullName }
javac -d out $main $example
java -cp out BasicUsageExample
```

## Local development

Compile:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName })
```

Run self-tests:

```powershell
$main = Get-ChildItem -Recurse -Filter *.java src\main\java | ForEach-Object { $_.FullName }
$test = Get-ChildItem -Recurse -Filter *.java src\test\java | ForEach-Object { $_.FullName }
javac -d out $main $test
java -cp out io.github.fightingdexiaoyu.uidshardrouter.UidShardRouterSelfTest
```

## Publish checklist

Before publishing to GitHub, replace the placeholder package coordinates if you want:

- `groupId`: `io.github.FightingDeXiaoyu`
- copyright holder in `LICENSE`
- repository URL in the README

Recommended repository metadata:

- topics: `java`, `sharding`, `routing`, `database`, `cli`
- short description: `Zero-dependency Java library for deterministic uid-based table routing`

Recommended repository URL:

- `https://github.com/FightingDeXiaoyu/uid-shard-router`

## Why it can support an OSS application

This repository is intentionally small but complete:

- clear user problem
- reusable code instead of demo-only code
- documentation and contribution guide
- license included
- tests included
- straightforward roadmap for future PRs

That makes it a credible seed project for an open source profile, especially if you keep iterating on examples, integrations, and published releases.

## Maintainer workflow

- Track user-facing changes in [CHANGELOG.md](./CHANGELOG.md)
- Use the built-in issue templates for bugs and feature requests
- Use the pull request template to keep reviews consistent
- Publish tagged releases as the feature set grows
