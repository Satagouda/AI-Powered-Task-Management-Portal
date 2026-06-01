CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users
(
    id UUID PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    email VARCHAR(255) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    first_name VARCHAR(100),

    last_name VARCHAR(100),

    phone_number VARCHAR(20),

    profile_image_url VARCHAR(500),

    is_locked BOOLEAN DEFAULT FALSE,

    failed_login_attempts INTEGER DEFAULT 0,

    is_email_verified BOOLEAN DEFAULT FALSE,

    is_phone_verified BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles
(
    user_id UUID NOT NULL,

    role_id BIGINT NOT NULL,

    PRIMARY KEY(user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY(user_id)
            REFERENCES users(id),

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY(role_id)
            REFERENCES roles(id)
);