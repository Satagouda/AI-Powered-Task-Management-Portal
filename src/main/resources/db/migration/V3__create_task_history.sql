CREATE TABLE task_history
(
    id UUID PRIMARY KEY,

    task_id UUID NOT NULL,

    action VARCHAR(50) NOT NULL,

    payload JSONB,

    previous_hash TEXT,

    current_hash TEXT,

    block_index INTEGER,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_task_history_task
        FOREIGN KEY (task_id)
            REFERENCES tasks(id)
);