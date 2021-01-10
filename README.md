# Manual
Консольное приложение - клиент, которое взаимодействует с сервером. 
Пользователь управляет клиентом с помощью команд
Список команд живет в модуле Client, в файле help.md

# Стак
- Netty-based server
- SQLlite db

# Клиент-серверное сетевое хранилище.

## Описание:

Приложение напоминает очень простой вариант s3, gcs, dropbox, minio.

## Требования:

### Функциональные:

#### Обязательно:

- Отправка файлов на сервер; (ProtoBuf / Serialization)
- Скачивание файлов с сервера; (ProtoBuf / Serialization)
- Ведение прав для пользователей; (mysql/sqllite)
1/2 Просмотр файлов на сервере для каждого пользователя; (commands)
1/2 - Удаление файлов локально/на сервере; (commands)
1/2 - Переименование файлов локально/на сервере; (commands)

#### Дополнительно:

1/3 Авторизация/Аутентификация; (mysql/sqllite)
- Регистрация пользователей; (mysql/sqllite) + commands
- Контрольную сумму передачи файлов; (?)
- Синхронизацию папки на клиенте и сервере; (commands)
- Sharing файлов между клиентами; (mysql/sqllite) + commands

### Нефункциональные:

#### Обязательно:

- Исходный код на github-е
- Netty, nio, io используем;
- Клиентское приложение: cli;
- Покрытие тестами;
- Help, man и тд для пользователя;

#### Дополнительно:

- Собирать приложения в jar;
- Клиентское приложение: cli, desktop;




# CloudStorage
My educational project with simple file cloud storage





