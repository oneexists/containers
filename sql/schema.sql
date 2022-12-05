drop database if exists containers_test;
create database containers_test;

use containers_test;

create table app_user (
	app_user_id bigint primary key auto_increment,
    user_role int not null,
    username varchar(255) not null,
    `password` varchar(2056) not null,
    is_account_non_expired boolean not null,
    is_account_non_locked boolean not null,
    is_credentials_non_expired boolean not null,
    is_enabled boolean not null,
    constraint uk_app_user_username
		unique(username)
);

delimiter //
create procedure set_known_good_state()
begin
	delete from app_user;
    alter table app_user auto_increment = 1;

    insert into app_user(user_role, username, `password`, is_account_non_expired, is_account_non_locked, is_credentials_non_expired, is_enabled) values
        (0, 'username', '$2a$10$bJ.Q1/9A/1i4LpO90CVnHO.DK464jvQnrXUo0QHJggWEhgLF3eElm', true, true, true, true);
end //
delimiter ;