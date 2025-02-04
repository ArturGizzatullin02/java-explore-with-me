CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat         FLOAT NOT NULL,
    lon         FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(100) NOT NULL,
    category_id        BIGINT       NOT NULL,
    description        TEXT         NOT NULL,
    event_date         TIMESTAMP    NOT NULL,
    location_id        BIGINT       NOT NULL,
    paid               BOOLEAN      NOT NULL,
    participant_limit  INT          NOT NULL,
    request_moderation BOOLEAN      NOT NULL,
    title              VARCHAR(100) NOT NULL,
    confirmed_requests INT          NOT NULL,
    created_on         TIMESTAMP    NOT NULL,
    initiator_id       BIGINT       NOT NULL,
    published_on       TIMESTAMP    NOT NULL,
    state              VARCHAR(9)   NOT NULL,
    views              INT          NOT NULL,

    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE SET NULL,
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES locations (location_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    requester_id BIGINT     NOT NULL,
    created      TIMESTAMP  NOT NULL,
    event_id     BIGINT     NOT NULL,
    status       VARCHAR(9) NOT NULL,

    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned         BOOLEAN      NOT NULL,
    title          VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id BIGINT,
    event_id       BIGINT,

    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (compilation_id) ON DELETE CASCADE,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (event_id) ON DELETE CASCADE
);