DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES
    ('User', 'user@yandex.ru', 'password'),
    ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES
    ('ROLE_USER', 100000),
    ('ROLE_ADMIN', 100001);

INSERT INTO meals (id, user_id, dateTime, description, calories)
VALUES
    (nextval('global_seq'),100000,'2019-10-11 09:00', 'Завтрак', 500),
    (nextval('global_seq'),100000,'2019-10-11 12:00', 'Обед', 1000),
    (nextval('global_seq'),100000,'2019-10-11 19:00', 'Ужин', 500),
    (nextval('global_seq'),100000,'2019-10-12 09:20', 'Завтрак', 1000),
    (nextval('global_seq'),100000,'2019-10-12 13:00', 'Обед', 500),
    (nextval('global_seq'),100000,'2019-10-12 20:00', 'Ужин', 510);