CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  TEXT                                    NOT NULL,
    email TEXT                                    NOT NULL,
    role  TEXT                                    NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name TEXT                                    NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         TEXT                                    NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    confirmed_requests BIGINT,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        TEXT                                    NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    location_lat       FLOAT                                   NOT NULL,
    location_lon       FLOAT                                   NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  BIGINT                                  NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    state              TEXT                                    NOT NULL,
    title              TEXT                                    NOT NULL,
    views              BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ratings
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT                                  NOT NULL,
    user_id  BIGINT                                  NOT NULL,
    type     TEXT                                    NOT NULL,
    value    INT                                     NOT NULL,
    CONSTRAINT pk_likes PRIMARY KEY (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title  TEXT                                    NOT NULL,
    pinned BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id),
    CONSTRAINT uq_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    status       TEXT,
    CONSTRAINT pk_request_id PRIMARY KEY (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
);