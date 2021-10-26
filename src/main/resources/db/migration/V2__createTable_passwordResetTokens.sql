create table password_reset_tokens
(
    id      bigint primary key auto_increment,
    token   varchar(255),
    user_id bigint,
    foreign key (user_id) references users (id)
);