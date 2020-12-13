# Клиент-серверное сетевое хранилище.

## Описание:

Приложение напоминает очень простой вариант s3, gcs, dropbox, minio.

## Требования:

### Функциональные:

#### Обязательно:

- Отправка файлов на сервер; (ProtoBuf / Serialization)
- Скачивание файлов с сервера; (ProtoBuf / Serialization)
- Ведение прав для пользователей; (mysql/sqllite)
- Просмотр файлов на сервере для каждого пользователя; (commands)
- Удаление файлов локально/на сервере; (commands)
- Переименование файлов локально/на сервере; (commands)

#### Дополнительно:

- Авторизация/Аутентификация; (mysql/sqllite)
- Регистрация пользователей; (mysql/sqllite) + commands
- Контрольную сумму передачи файлов; (?)
- Синхронизацию папки на клиенте и сервере; (commands)
- Sharing файлов между клиентами; (mysql/sqllite) + commands

### Нефункциональные:

#### Обязательно:

- Исходный код на github-е
- Стек EE, Spring... не используем;
- Netty, nio, io используем;
- Клиентское приложение: cli, desktop;
- Покрытие тестами;
- Help, man и тд для пользователя;

#### Дополнительно:

- Собирать приложения в jar;
- Клиентское приложение: cli, desktop;




# CloudStorage
My educational project with simple file cloud storage

Defenition of done. Cloud can:
1. Accept clients (Socket / SocketChannel)
2. Recieve and save file (ProtoBuf / Serialization)
3. Send file (ProtoBuf / Serialization)
4. Rename file (Commands)
5. Delete file (Commands)
6. Send file list from folder (Commands)
7. Auth clinet (postgreSQL)
8. Register client (postgreSQL)
9. Navigate client 


Stack:
Netty server
IO + javaFX client
postgreSQL


