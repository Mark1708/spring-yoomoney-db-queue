CREATE TABLE queue_tasks_1
(
    id                BIGSERIAL PRIMARY KEY,
    queue_name        TEXT NOT NULL,
    payload           TEXT,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now(),
    next_process_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    attempt           INTEGER                  DEFAULT 0,
    reenqueue_attempt INTEGER                  DEFAULT 0,
    total_attempt     INTEGER                  DEFAULT 0
);

CREATE TABLE queue_tasks_2
(
    id                BIGSERIAL PRIMARY KEY,
    queue_name        TEXT NOT NULL,
    payload           TEXT,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now(),
    next_process_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    attempt           INTEGER                  DEFAULT 0,
    reenqueue_attempt INTEGER                  DEFAULT 0,
    total_attempt     INTEGER                  DEFAULT 0
) WITH (
      autovacuum_vacuum_cost_delay = 5,
      autovacuum_vacuum_cost_limit = 500,
      autovacuum_vacuum_scale_factor = 0.0001
      );
CREATE INDEX queue_tasks_name_time_desc_idx
    ON queue_tasks_2 USING btree (queue_name, next_process_at, id DESC);

CREATE TABLE queue_tasks_3
(
    id                bigserial   NOT NULL,
    queue_name        text        NOT NULL,
    payload           text        NULL,
    created_at        timestamptz NULL DEFAULT now(),
    next_process_at   timestamptz NULL DEFAULT now(),
    attempt           int4        NULL DEFAULT 0,
    reenqueue_attempt int4        NULL DEFAULT 0,
    total_attempt     int4        NULL DEFAULT 0,
    request_id        text        NULL,
    CONSTRAINT queue_tasks_o_pkey PRIMARY KEY (id)
)
    WITH (
        fillfactor = 30,
        autovacuum_vacuum_cost_delay = 5,
        autovacuum_vacuum_cost_limit = 500,
        autovacuum_vacuum_scale_factor = 0.0001
        );
CREATE UNIQUE INDEX queue_tasks_o_name_time_desc_idx ON queue_tasks_3 USING btree (queue_name, next_process_at, id DESC) WITH (fillfactor ='30');

CREATE TABLE queue_tasks_4
(
    id                bigserial PRIMARY KEY,
    queue_name        text        NOT NULL,
    payload           text        NULL,
    created_at        timestamptz NULL DEFAULT now(),
    next_process_at   timestamptz NULL DEFAULT now(),
    attempt           int4        NULL DEFAULT 0,
    reenqueue_attempt int4        NULL DEFAULT 0,
    total_attempt     int4        NULL DEFAULT 0,
    request_id        text        NULL
)
    PARTITION BY hash (id);

CREATE UNIQUE INDEX queue_tasks_4_name_time_desc_idx ON queue_tasks_4 USING btree (queue_name, next_process_at, id DESC) WITH (fillfactor ='30');

CREATE TABLE queue_tasks_4_0 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 0);
CREATE TABLE queue_tasks_4_1 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 1);
CREATE TABLE queue_tasks_4_2 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 2);
CREATE TABLE queue_tasks_4_3 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 3);
CREATE TABLE queue_tasks_4_4 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 4);
CREATE TABLE queue_tasks_4_5 PARTITION OF queue_tasks_4 FOR VALUES WITH (modulus 6, remainder 5);

ALTER TABLE queue_tasks_4_0
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_4_1
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_4_2
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_4_3
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_4_4
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_4_5
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);

CREATE TABLE queue_tasks_5
(
    id                bigserial PRIMARY KEY,
    queue_name        text        NOT NULL,
    payload           text        NULL,
    created_at        timestamptz NULL DEFAULT now(),
    next_process_at   timestamptz NULL DEFAULT now(),
    attempt           int4        NULL DEFAULT 0,
    reenqueue_attempt int4        NULL DEFAULT 0,
    total_attempt     int4        NULL DEFAULT 0,
    request_id        text        NULL
)
    PARTITION BY hash (id);

CREATE UNIQUE INDEX queue_tasks_5_name_time_desc_idx ON queue_tasks_5 USING btree (queue_name, next_process_at, id DESC) WITH (fillfactor ='30');

CREATE TABLE queue_tasks_5_0 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 0);
CREATE TABLE queue_tasks_5_1 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 1);
CREATE TABLE queue_tasks_5_2 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 2);
CREATE TABLE queue_tasks_5_3 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 3);
CREATE TABLE queue_tasks_5_4 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 4);
CREATE TABLE queue_tasks_5_5 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 5);
CREATE TABLE queue_tasks_5_6 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 6);
CREATE TABLE queue_tasks_5_7 PARTITION OF queue_tasks_5 FOR VALUES WITH (modulus 8, remainder 7);

ALTER TABLE queue_tasks_5_0
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_1
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_2
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_3
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_4
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_5
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_6
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_5_7
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);

CREATE TABLE queue_tasks_6
(
    id                bigserial PRIMARY KEY,
    queue_name        text        NOT NULL,
    payload           text        NULL,
    created_at        timestamptz NULL DEFAULT now(),
    next_process_at   timestamptz NULL DEFAULT now(),
    attempt           int4        NULL DEFAULT 0,
    reenqueue_attempt int4        NULL DEFAULT 0,
    total_attempt     int4        NULL DEFAULT 0,
    request_id        text        NULL
)
    PARTITION BY hash (id);

CREATE UNIQUE INDEX queue_tasks_6_name_time_desc_idx ON queue_tasks_6 USING btree (queue_name, next_process_at, id DESC) WITH (fillfactor ='30');

CREATE TABLE queue_tasks_6_0 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 0);
CREATE TABLE queue_tasks_6_1 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 1);
CREATE TABLE queue_tasks_6_2 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 2);
CREATE TABLE queue_tasks_6_3 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 3);
CREATE TABLE queue_tasks_6_4 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 4);
CREATE TABLE queue_tasks_6_5 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 5);
CREATE TABLE queue_tasks_6_6 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 6);
CREATE TABLE queue_tasks_6_7 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 7);
CREATE TABLE queue_tasks_6_8 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 8);
CREATE TABLE queue_tasks_6_9 PARTITION OF queue_tasks_6 FOR VALUES WITH (modulus 10, remainder 9);

ALTER TABLE queue_tasks_6_0
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_1
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_2
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_3
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_4
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_5
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_6
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_7
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_8
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_6_9
    SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);