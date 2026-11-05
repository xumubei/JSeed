CREATE TABLE demo_item (id BIGINT PRIMARY KEY, department_id BIGINT NOT NULL, label VARCHAR(100));
INSERT INTO demo_item VALUES (1, 10, 'visible'), (2, 20, 'filtered');
CREATE TABLE demo_partition_1 (id BIGINT PRIMARY KEY, department_id BIGINT NOT NULL, label VARCHAR(100));
INSERT INTO demo_partition_1 VALUES (1, 10, 'partition one'), (2, 20, 'filtered partition row');
