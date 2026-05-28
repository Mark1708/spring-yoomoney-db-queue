# spring-yoomoney-db-queue

[Русская версия](README.ru.md)

![Java](https://img.shields.io/badge/Java-21-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-queue%20backend-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Status](https://img.shields.io/badge/status-research%20snapshot-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)

PostgreSQL-backed queue benchmark for comparing table layouts, batching, and partitioning in a Spring Boot producer/consumer system.

## What this repository answers

The experiment asks a practical backend question: if a service already uses PostgreSQL, how far can a PostgreSQL-backed queue be pushed before a dedicated broker becomes the obvious choice?

The repository preserves:

- a small local `db-queue` implementation;
- a Spring Boot starter around that queue;
- producer and consumer services;
- a small benchmark harness;
- SQL variants for different queue table layouts;
- preserved benchmark charts and a presentation deck.

This is an experiment log and runnable demo, not a recommendation to replace RabbitMQ, Kafka, or another broker with PostgreSQL for new high-load systems.

## At a glance

| Field | Value |
| --- | --- |
| Repository type | Backend benchmark / research snapshot |
| Runtime | Java 21 |
| Framework | Spring Boot 3.2.2 |
| Build tool | Gradle multi-project build |
| Storage | PostgreSQL |
| Benchmark telemetry | RabbitMQ + `test-app` persistence database |
| Main result | `queue_tasks_5` with batch size `10` is the best configuration in the preserved comparison table |

## Architecture

```text
test-app
  POST /?testDataCount=N
        |
        v
producer-app
  buffers 10 messages, then writes a batch to db-queue
        |
        v
PostgreSQL queue table: queue_tasks_1 .. queue_tasks_6
        |
        v
consumer-app
  polls with batch-size 10 and thread-count 14
        |
        v
RabbitMQ
  carries per-message processing statistics
        |
        v
test-app
  stores stats in test-db and exposes them via GET /
```

## Modules

| Module | Purpose |
| --- | --- |
| `db-queue` | Local Java implementation of a queue stored in PostgreSQL. |
| `spring-boot-starter-db-queue` | Spring Boot auto-configuration for the local queue module. |
| `common` | Shared DTOs used by the benchmark services. |
| `producer-app` | HTTP service that accepts benchmark messages and writes them to the queue in batches of 10. |
| `consumer-app` | Queue worker that polls PostgreSQL and sends processing statistics to RabbitMQ. |
| `test-app` | Benchmark controller: starts runs, calls producers, records statistics, and returns stored results. |

## Queue table variants

`sql/create_tables.sql` defines six queue tables. The preserved benchmark table compares variants `queue_tasks_1` through `queue_tasks_5`; `queue_tasks_6` exists in SQL but is not part of the preserved result table.

| Variant | Layout |
| --- | --- |
| `queue_tasks_1` | Baseline table. |
| `queue_tasks_2` | Baseline plus autovacuum settings and btree index. |
| `queue_tasks_3` | Adds `fillfactor = 30` for table and index storage. |
| `queue_tasks_4` | Hash-partitioned table with 6 partitions. |
| `queue_tasks_5` | Hash-partitioned table with 8 partitions. |
| `queue_tasks_6` | Hash-partitioned table with 10 partitions; present as an additional SQL variant. |

## Local run

The compose stack uses prebuilt images from `ghcr.io/mark1708/*`. It is useful for running the preserved benchmark setup, not for proving that the local source tree currently builds.

```sh
cp .env.example .env
docker compose up -d
```

Compose starts:

| Service | Purpose | Local endpoint |
| --- | --- | --- |
| `producer-app` | Writes messages into the PostgreSQL-backed queue | `http://localhost:8070/` |
| `consumer-app` | Polls the queue and emits processing stats | `http://localhost:8090/` |
| `test-app` | Starts benchmark runs and returns saved results | `http://localhost:8080/` |
| `db-queue` | Queue PostgreSQL database | `localhost:5432` |
| `test-db` | Metrics PostgreSQL database | `localhost:5433` |
| `rabbit-mq` | RabbitMQ broker and management UI | `localhost:5672`, `http://localhost:15672/` |

Start a small run through the harness:

```sh
curl -X POST 'http://localhost:8080/?testDataCount=10000'
curl http://localhost:8080/
```

By default, compose configures producer and consumer with `DB_QUEUE_LOCATION=queue_tasks_1`. To compare another table variant, update that environment variable for both services and recreate the relevant containers. For clean comparisons, reset the PostgreSQL volume or otherwise clear queue state between runs.

## Reproducing the full comparison

The repository contains the building blocks for the experiment, but it does not contain a one-command benchmark runner or chart regeneration pipeline. A manual reproduction requires:

1. Start PostgreSQL, RabbitMQ, producer, consumer, and test-app.
2. Choose a queue table with `DB_QUEUE_LOCATION`.
3. Run the `test-app` POST endpoint with the same message count for each variant.
4. Read stored results from `GET /` on `test-app`.
5. Reset queue/test state between variants.
6. Recreate charts manually from the collected measurements.

## Preserved comparison table

The table below is the preserved comparison snapshot from the project documentation. The deltas are relative to the baseline row.

| Configuration | Batch | RPS | Delta | Delta % |
| --- | ---: | ---: | ---: | ---: |
| `queue_tasks_1` | 1 | 334,69 | baseline | baseline |
| `queue_tasks_1` | 10 | 1222,82 | 888,12 | 265,35 |
| `queue_tasks_2` | 10 | 1275,85 | 941,15 | 281,20 |
| `queue_tasks_3` | 10 | 1230,76 | 896,06 | 267,73 |
| `queue_tasks_4` | 10 | 1310,81 | 976,11 | 291,64 |
| `queue_tasks_5` | 10 | 1314,12 | 979,42 | 292,63 |
| `queue_tasks_5` | 20 | 1289,58 | 954,88 | 285,30 |

The best row in this table is `queue_tasks_5` with batch size `10`: hash partitioning with 8 partitions and tuned storage parameters.

## Charts and provenance

The repository keeps three result artifacts:

| Artifact | Meaning |
| --- | --- |
| [`assets/presentation.pdf`](assets/presentation.pdf) | Original presentation with methodology, table variants, result table, and conclusions. |
| [`assets/latency.png`](assets/latency.png) | Latency chart with min/avg/max latency over accumulated messages. |
| [`assets/RPS.png`](assets/RPS.png) | Runtime RPS chart from a benchmark run, with visible throughput around the 2k+ RPS range. |

The RPS chart is a time-series from a run, while the comparison table is an aggregated variant comparison. They should not be read as the same metric without the original measurement notes.

<p align="center">
  <img width="auto" height="300" src="assets/latency.png">
</p>
<p align="center">Latency: min / average / max</p>

<p align="center">
  <img width="auto" height="250" src="assets/RPS.png">
</p>
<p align="center">Runtime requests per second</p>

## Limitations

- The benchmark numbers are preserved results, not freshly regenerated measurements.
- Hardware, PostgreSQL settings, container state, dataset size, table choice, and service configuration can change the outcome.
- Compose runs prebuilt container images; local source builds are a separate verification step.
- `queue_tasks_6` exists in SQL but has no preserved row in the comparison table.
- The result artifacts are preserved manually; no chart regeneration script is included.
- Keep real credentials in local `.env` files and do not commit them.

## Reference

- Upstream inspiration: [db-queue/db-queue](https://github.com/db-queue/db-queue)
- SQL variants: [`sql/create_tables.sql`](sql/create_tables.sql)
- Benchmark deck: [`assets/presentation.pdf`](assets/presentation.pdf)

## Status

Backend/research project. The repository preserves benchmark context, implementation notes, and reproducibility guidance; it is not maintained as a production-ready queue service.
