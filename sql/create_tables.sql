CREATE TABLE queue_tasks_h_8 (
                                     id bigserial PRIMARY KEY,
                                     queue_name text NOT NULL,
                                     payload text NULL,
                                     created_at timestamptz NULL DEFAULT now(),
                                     next_process_at timestamptz NULL DEFAULT now(),
                                     attempt int4 NULL DEFAULT 0,
                                     reenqueue_attempt int4 NULL DEFAULT 0,
                                     total_attempt int4 NULL DEFAULT 0,
                                     request_id text NULL
)
    PARTITION BY hash (id);

CREATE UNIQUE INDEX queue_tasks_h_8_name_time_desc_idx ON queue_tasks_h_8 USING btree (queue_name, next_process_at, id DESC) WITH (fillfactor='30');

CREATE TABLE queue_tasks_h_8_0 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 0);
CREATE TABLE queue_tasks_h_8_1 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 1);
CREATE TABLE queue_tasks_h_8_2 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 2);
CREATE TABLE queue_tasks_h_8_3 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 3);
CREATE TABLE queue_tasks_h_8_4 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 4);
CREATE TABLE queue_tasks_h_8_5 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 5);
CREATE TABLE queue_tasks_h_8_6 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 6);
CREATE TABLE queue_tasks_h_8_7 PARTITION OF queue_tasks_h_8 FOR VALUES WITH (modulus 8, remainder 7);

ALTER TABLE queue_tasks_h_8_0 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_1 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_2 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_3 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_4 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_5 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_6 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);
ALTER TABLE queue_tasks_h_8_7 SET (fillfactor = 30, autovacuum_vacuum_cost_delay = 5, autovacuum_vacuum_cost_limit = 500, autovacuum_vacuum_scale_factor = 0.0001);