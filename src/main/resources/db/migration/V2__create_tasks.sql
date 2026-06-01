CREATE TABLE tasks
(
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    title VARCHAR(255) NOT NULL,

    description TEXT,

    priority VARCHAR(20) NOT NULL,

    status VARCHAR(20) NOT NULL,

    due_date DATE,

    estimated_effort INTEGER,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_task_user
        FOREIGN KEY(user_id)
            REFERENCES users(id)
);

ALTER TABLE tasks
    ADD COLUMN deleted BOOLEAN DEFAULT FALSE;