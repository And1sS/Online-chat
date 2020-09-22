CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS account_info(
    id UUID DEFAULT uuid_generate_v4(),
	name TEXT NOT NULL,
	surname TEXT NOT NULL,
    login TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),

	PRIMARY KEY(id),
    CONSTRAINT unique_login_constraint UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS access_token(
    id UUID DEFAULT uuid_generate_v4(),
    token UUID DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY(id),
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS group_chat(
	id UUID DEFAULT uuid_generate_v4(),
	title TEXT NOT NULL,
    about TEXT,
	creator_id UUID,

	PRIMARY KEY(id),
    CONSTRAINT creator_id_constraint FOREIGN KEY (creator_id) REFERENCES account_info (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS group_message(
    id UUID DEFAULT uuid_generate_v4(),
    author_id UUID,
    chat_id UUID NOT NULL,
    contents TEXT NOT NULL, 
    creation_time TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY(id),
    CONSTRAINT chat_id_constraint FOREIGN KEY (chat_id) REFERENCES group_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (author_id) REFERENCES account_info (id) ON DELETE SET NULL
);

CREATE TYPE MemberType AS ENUM ('read', 'readwrite', 'admin');

CREATE TABLE IF NOT EXISTS group_user(
    group_chat_id UUID NOT NULL,    
    user_id UUID NOT NULL,
    member_type MemberType NOT NULL,

    CONSTRAINT group_user_unique_constraint UNIQUE (group_chat_id, user_id),
    CONSTRAINT group_chat_id_constraint FOREIGN KEY (group_chat_id) REFERENCES group_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS private_chat(
    id UUID DEFAULT uuid_generate_v4(),
    user_1_id UUID,
    user_2_id UUID,

    PRIMARY KEY(id),
    CONSTRAINT user_1_constraint FOREIGN KEY (user_1_id) REFERENCES account_info (id) ON DELETE SET NULL,
    CONSTRAINT user_2_constraint FOREIGN KEY (user_2_id) REFERENCES account_info (id) ON DELETE SET NULL, 
    CONSTRAINT unique_private_chat_constraint UNIQUE (user_1_id, user_2_id)
);

CREATE TABLE IF NOT EXISTS private_message(
    id UUID DEFAULT uuid_generate_v4(),
    user_id UUID,
    chat_id UUID NOT NULL,
    contents TEXT NOT NULL, 
    creation_time TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY(id),
    CONSTRAINT chat_id_constraint FOREIGN KEY (chat_id) REFERENCES private_chat (id) ON DELETE CASCADE,
    CONSTRAINT user_id_constraint FOREIGN KEY (user_id) REFERENCES account_info (id) ON DELETE SET NULL
);