insert into users (id, login, password) values (1, 'admin@test.com', '$2a$12$pBqGg4hhFmlYJuRCNadI3ehWlX6EnLdpHgQRnkIinsh6rIPm6ZbyC');
insert into users (id, login, password) values (2, 'user@test.com', '$2a$12$50fw4HdLHI1Yt1bvPl6eSOK26ndkESQdB6crjzxfX0t/a5IFneeXO');
insert into user_roles (id, roles, user_id) values (1, '1', 1);
insert into user_roles (id, roles, user_id) values (2, '0', 2);