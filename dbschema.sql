CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS account_info
(
    id            UUID      DEFAULT uuid_generate_v4(),
    name          TEXT NOT NULL,
    surname       TEXT NOT NULL,
    login         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at    TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT unique_login_constraint UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS account_log
(
    id         UUID DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    action     TEXT NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS access_token
(
    id         UUID      DEFAULT uuid_generate_v4(),
    token      UUID      DEFAULT uuid_generate_v4(),
    user_id    UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_chat
(
    id         UUID DEFAULT uuid_generate_v4(),
    title      TEXT NOT NULL,
    about      TEXT,
    creator_id UUID,

    PRIMARY KEY (id),
    CONSTRAINT creator_id_constraint FOREIGN KEY (creator_id) REFERENCES account_info (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS group_message
(
    id            UUID      DEFAULT uuid_generate_v4(),
    author_id     UUID,
    chat_id       UUID NOT NULL,
    contents      TEXT NOT NULL,
    creation_time TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT chat_id_constraint FOREIGN KEY (chat_id) REFERENCES group_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (author_id) REFERENCES account_info (id) ON DELETE SET NULL
);


CREATE TYPE MemberType AS ENUM ('read', 'readwrite', 'admin');
CREATE CAST (character varying AS MemberType) WITH INOUT AS ASSIGNMENT;

CREATE TABLE IF NOT EXISTS group_user
(
    group_chat_id UUID       NOT NULL,
    user_id       UUID       NOT NULL,
    member_type   MemberType NOT NULL,

    CONSTRAINT group_user_unique_constraint UNIQUE (group_chat_id, user_id),
    CONSTRAINT group_chat_id_constraint FOREIGN KEY (group_chat_id) REFERENCES group_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS private_chat
(
    id        UUID DEFAULT uuid_generate_v4(),
    user_1_id UUID,
    user_2_id UUID,

    PRIMARY KEY (id),
    CONSTRAINT user_1_constraint FOREIGN KEY (user_1_id) REFERENCES account_info (id) ON DELETE SET NULL,
    CONSTRAINT user_2_constraint FOREIGN KEY (user_2_id) REFERENCES account_info (id) ON DELETE SET NULL,
    CONSTRAINT unique_private_chat_constraint UNIQUE (user_1_id, user_2_id)
);

CREATE TABLE IF NOT EXISTS private_message
(
    id            UUID      DEFAULT uuid_generate_v4(),
    user_id       UUID,
    chat_id       UUID NOT NULL,
    contents      TEXT NOT NULL,
    creation_time TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (id),
    CONSTRAINT chat_id_constraint FOREIGN KEY (chat_id) REFERENCES private_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE SET NULL
);

CREATE OR REPLACE PROCEDURE insert_user(_name TEXT,
                                        _surname TEXT,
                                        _login TEXT,
                                        _password_hash TEXT)
AS
$$
INSERT INTO account_info (name, surname, login, password_hash)
VALUES (_name, _surname, _login, _password_hash)
$$ LANGUAGE SQL;

CREATE OR REPLACE PROCEDURE update_account_password(_login TEXT,
                                                    _password_hash TEXT)
AS
$$
UPDATE account_info
SET password_hash=_password_hash
WHERE login = _login;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION count_users() RETURNS BIGINT
AS
$$
SELECT COUNT(*)
FROM account_info
$$ LANGUAGE SQL;

CREATE OR REPLACE PROCEDURE delete_user_by_id(
    _login TEXT
)
AS
$$
DELETE
FROM account_info
WHERE login = _login
$$ LANGUAGE SQL;

CREATE OR REPLACE PROCEDURE delete_user_by_login(
    _login TEXT
)
AS
$$
DELETE
FROM account_info
WHERE login = _login
$$ LANGUAGE SQL;


CREATE OR REPLACE FUNCTION account_log() RETURNS TRIGGER AS
$$
DECLARE
    _action TEXT;
BEGIN
    _action = tg_argv[0];
    INSERT
    INTO account_log (account_id, action)
    VALUES (new.id, _action);
    RETURN new;
END;
$$ LANGUAGE PLPGSQL;


CREATE TRIGGER on_user_insert
    AFTER INSERT
    ON account_info
    FOR EACH ROW
EXECUTE PROCEDURE account_log('INSERT');

CREATE TRIGGER on_user_update
    AFTER UPDATE
    ON account_info
    FOR EACH ROW
EXECUTE PROCEDURE account_log('UPDATE');

CREATE VIEW view_1 AS 
SELECT * FROM account_info;

CREATE VIEW view_2 AS 
SELECT * FROM group_chat WHERE about IS NOT NULL;

CREATE VIEW view_3 AS 
SELECT * FROM group_chat WHERE(about IS NOT NULL AND creator_id IS NOT NULL);

CREATE VIEW view_4 AS 
SELECT id, created_at FROM account_info;

CREATE VIEW view_5 AS 
SELECT id AS account_id, created_at AS creation_date_and_time FROM account_info;

CREATE VIEW view_6 AS 
SELECT 
'User with id: ' || user_id || 'is member of group chat with id: ' ||
group_chat_id || 'with ' || member_type || 'privileges.' 
AS group_chat_membership FROM group_user;

CREATE VIEW view_7 AS 
SELECT id, title, 
    CASE 
        WHEN about IS NULL THEN 'NO_DESCRIPTION'
        ELSE about
    END AS about, 
    CASE 
        WHEN creator_id IS NULL THEN 'DELETED_USER'
        ELSE creator_id::TEXT
    END AS creator
FROM group_chat;

CREATE VIEW view_8 AS 
SELECT * FROM account_info ORDER BY created_at DESC LIMIT 100;

CREATE VIEW view_9 AS
SELECT * FROM account_info ORDER BY random() LIMIT 5; 

CREATE VIEW view_10 AS
SELECT * FROM group_chat WHERE creator_id IS NULL;

CREATE VIEW view_11 AS
SELECT * FROM account_info WHERE name LIKE '%ndrew%';

CREATE VIEW view_12 AS
SELECT * FROM account_info ORDER BY created_at DESC LIMIT 100;

CREATE VIEW view_13 AS
SELECT * FROM account_info ORDER BY created_at DESC, name ASC LIMIT 100;

CREATE VIEW view_14 AS
SELECT * FROM account_info ORDER BY substr(name, 0, 3) ASC LIMIT 100;

CREATE VIEW view_15 AS
SELECT id, title, about, creator_id FROM (
    SELECT id, title, about, creator_id,
        CASE 
            WHEN creator_id IS NULL THEN 0
            ELSE 1
        END AS is_null
    FROM group_chat
) _
ORDER BY is_null DESC;

CREATE VIEW view_16 AS 
SELECT * FROM group_chat ORDER BY
CASE
    WHEN about IS NULL THEN title
    ELSE about
END;

CREATE TABLE IF NOT EXISTS test_table_1(
    id UUID DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS test_table_2(
    id UUID DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL
);

CREATE VIEW view_17 AS
SELECT * FROM test_table_1
UNION ALL
SELECT * FROM test_table_2;

CREATE VIEW view_18 AS
SELECT u.id AS user_id, gc.id AS group_chat_id, gc.title AS group_chat_title
FROM account_info u 
INNER JOIN group_user gu ON u.id = gu.user_id
INNER JOIN group_chat gc ON gu.group_chat_id = gc.id;

CREATE VIEW view_19 AS
SELECT t1.id, t1.name AS t1_name, t2.name AS t2_name
FROM test_table_1 t1
INNER JOIN test_table_2 t2 ON t1.id = t2.id;

CREATE VIEW view_20 AS
SELECT * FROM test_table_1
EXCEPT
SELECT * FROM test_table_2;
 
CREATE VIEW view_21 AS
SELECT t1.* FROM test_table_1 t1
LEFT OUTER JOIN test_table_2 t2 ON t1.id = t2.id
WHERE t2.name IS NULL; 

CREATE VIEW view_22 AS
SELECT t1.id, t1.name AS t1_name, t2.name AS t2_name
FROM test_table_1 t1
LEFT OUTER JOIN test_table_2 t2 ON t1.id = t2.id;

CREATE TABLE IF NOT EXISTS test_table_3(
    id UUID DEFAULT uuid_generate_v4(),
    salary DECIMAL(1000, 10)
);

-- CREATE VIEW view_23 AS
-- SELECT 

CREATE VIEW view_25 AS
SELECT t1.*, t3.salary FROM test_table_1 t1
FULL OUTER JOIN test_table_3 t3 ON t1.id = t3.id;

CREATE VIEW view_26 AS
SELECT * FROM test_table_3 
WHERE coalesce(salary, 0) >= 50;

CREATE TABLE IF NOT EXISTS t15(
    id SERIAL
);

CREATE VIEW view_27 AS
SELECT substring(ts.name, index.pos::INT, 1) AS character
FROM (SELECT name FROM test_table_1 WHERE name = 'TEST STRING') ts,
     (SELECT id AS pos FROM t15) index
WHERE index.pos <= length(ts.name);

CREATE VIEW view_28 AS
SELECT 'hi, this is test of ''';
