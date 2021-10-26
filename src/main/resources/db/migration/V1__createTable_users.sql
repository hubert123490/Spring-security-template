CREATE table users
(
    id                        bigint primary key auto_increment,
    user_id                   varchar(255) not null,
    first_name                varchar(50)  not null,
    last_name                 varchar(50)  not null,
    email                     varchar(120) not null unique,
    encrypted_password        varchar(255),
    email_verification_token  varchar(255),
    email_verification_status bit          not null
);