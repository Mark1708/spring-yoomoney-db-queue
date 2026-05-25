# spring-yoomoney-db-queue

Backend research experiment on PostgreSQL-backed message queues.

[Русская версия](README.ru.md)

## Project scope

This repository explores how PostgreSQL table design affects queue throughput:

- baseline table
- autovacuum tuning
- btree index
- fillfactor tuning
- hash partitioning

The goal was to understand which layout gives the best write/read throughput and whether PostgreSQL can be a practical queue backend for high-load services.

## Research focus

- build a small queue library on top of PostgreSQL
- prepare reproducible test data and schema variants
- run producer/consumer load tests
- compare RPS, latency, and stability under load

## Results

The best result in this setup came from `queue_tasks_5` with batch size `10`.

| Configuration | Batch | RPS | Delta | Delta % |
|---|---:|---:|---:|---:|
| queue_tasks_1 | 1 | 334,69 | baseline | baseline |
| queue_tasks_1 | 10 | 1222,82 | 888,12 | 265,35 |
| queue_tasks_2 | 10 | 1275,85 | 941,15 | 281,20 |
| queue_tasks_3 | 10 | 1230,76 | 896,06 | 267,73 |
| queue_tasks_4 | 10 | 1310,81 | 976,11 | 291,64 |
| queue_tasks_5 | 10 | 1314,12 | 979,42 | 292,63 |
| queue_tasks_5 | 20 | 1289,58 | 954,88 | 285,30 |

Conclusion: hash partitioning with tuned storage parameters provided the most stable throughput in this benchmark.

## Visuals

<p align="center">
  <img width="auto" height="300" src="assets/latency.png">
</p>
<p align="center">Latency (min/avg/max)</p>

<p align="center">
  <img width="auto" height="250" src="assets/RPS.png">
</p>
<p align="center">Requests per second</p>

## Reproducibility

Copy the environment template and start the local stack:

```sh
cp .env.example .env
docker compose up -d
```

## Stack

- Spring Boot 3
- Java 21
- PostgreSQL
- db-queue

## Notes

- This is a research prototype, not a maintained backend service.
- The benchmark narrative and schema variants are preserved for comparison.
- Inspiration: [db-queue](https://github.com/db-queue/db-queue).

