DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, user_id)
VALUES ('2020-10-15 10:00', 'breakfast', 500, 100000),
       ('2020-10-15 13:00', 'lunch', 1000, 100000),
       ('2020-10-15 20:00', 'dinner', 500, 100000),
       ('2020-10-16 00:00', 'night food', 100, 100000),
       ('2020-10-16 10:00', 'breakfast', 500, 100000),
       ('2020-10-16 13:00', 'lunch', 1000, 100000),
       ('2020-10-16 20:00', 'dinner', 500, 100000),
       ('2020-10-15 10:00', 'breakfast', 500, 100001),
       ('2020-10-15 13:00', 'lunch', 1000, 100001),
       ('2020-10-15 20:00', 'dinner', 500, 100001),
       ('2020-10-16 00:00', 'night food', 100, 100001),
       ('2020-10-16 10:00', 'breakfast', 500, 100001),
       ('2020-10-16 13:00', 'lunch', 1000, 100001),
       ('2020-10-16 20:00', 'dinner', 500, 100001);
