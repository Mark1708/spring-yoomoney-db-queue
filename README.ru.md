# spring-yoomoney-db-queue

Backend research experiment по PostgreSQL-backed message queues.

[English version](README.md)

## Назначение проекта

Репозиторий исследует, как структура таблиц PostgreSQL влияет на throughput очереди:

- baseline table
- autovacuum tuning
- btree index
- fillfactor tuning
- hash partitioning

Цель эксперимента - понять, какая схема даёт лучший write/read throughput и может ли PostgreSQL быть практичным queue backend для high-load сервисов.

## Фокус исследования

- построить небольшую queue library поверх PostgreSQL
- подготовить воспроизводимые test data и варианты схем
- запустить producer/consumer load tests
- сравнить RPS, latency и стабильность под нагрузкой

## Результаты

Лучший результат в этом setup показала конфигурация `queue_tasks_5` с batch size `10`.

| Configuration | Batch | RPS | Delta | Delta % |
|---|---:|---:|---:|---:|
| queue_tasks_1 | 1 | 334,69 | baseline | baseline |
| queue_tasks_1 | 10 | 1222,82 | 888,12 | 265,35 |
| queue_tasks_2 | 10 | 1275,85 | 941,15 | 281,20 |
| queue_tasks_3 | 10 | 1230,76 | 896,06 | 267,73 |
| queue_tasks_4 | 10 | 1310,81 | 976,11 | 291,64 |
| queue_tasks_5 | 10 | 1314,12 | 979,42 | 292,63 |
| queue_tasks_5 | 20 | 1289,58 | 954,88 | 285,30 |

Вывод: hash partitioning с tuned storage parameters показал самый стабильный throughput в этом benchmark.

## Графики

<p align="center">
  <img width="auto" height="300" src="assets/latency.png">
</p>
<p align="center">Latency (min/avg/max)</p>

<p align="center">
  <img width="auto" height="250" src="assets/RPS.png">
</p>
<p align="center">Requests per second</p>

## Воспроизводимость

Скопируйте шаблон окружения и запустите локальный стек:

```sh
cp .env.example .env
docker compose up -d
```

## Стек

- Spring Boot 3
- Java 21
- PostgreSQL
- db-queue

## Примечания

- Это research prototype, а не поддерживаемый backend service.
- Benchmark narrative и варианты схем сохранены для сравнения.
- Inspiration: [db-queue](https://github.com/db-queue/db-queue).
