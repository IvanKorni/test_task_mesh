# test-task-mesh

Простой REST API для тестового задания с пользователями, контактами, балансом и переводами.

## Что реализовано

- Вход по email или телефону с выдачей JWT.
- Поиск пользователей по телефону, email, префиксу имени и дате рождения.
- Добавление, изменение и удаление email/телефонов текущего пользователя.
- Перевод денег другому пользователю с блокировкой счетов в базе.
- Автоматическое увеличение баланса на 10% каждые 30 секунд, но не больше 207% от начального баланса.

## Стек

- Java 11
- Spring Boot 2.7
- PostgreSQL
- Liquibase
- Spring Security + JWT
- OpenAPI Generator
- MapStruct
- Testcontainers

## Запуск

Создать базу PostgreSQL:

```sql
create database demo1;
```

Создать `.env` из примера:

```bash
cp .env.example .env
```

Если нужно, поменять `DB_USERNAME` и `DB_PASSWORD` в `.env`, затем запустить приложение:

```bash
./mvnw spring-boot:run
```

API будет доступен здесь:

- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Тестовые пользователи

Liquibase создает трех пользователей:

| Логин | Пароль |
| --- | --- |
| `ivan.petrov@example.com` или `79207865432` | `password1` |
| `anna.sidorova@example.com` или `79207865433` | `password2` |
| `petr.ivanov@example.com` или `79207865434` | `password3` |

Получить JWT:

```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"login":"ivan.petrov@example.com","password":"password1"}'
```

Передавать токен в защищенные методы:

```bash
curl http://localhost:8080/v1/users/search?name=Ivan \
  -H "Authorization: Bearer <token>"
```

## Тесты

Запустить все тесты:

```bash
./mvnw test
```

Docker должен быть запущен, потому что интеграционные тесты используют Testcontainers.

## Дополнительно

- Postman-коллекция лежит в `src/main/java/com/mesh/test_task/postman`.
- Пароли тестовых пользователей хранятся открытым текстом только для простоты тестового задания.
- Runtime-настройки лежат в `.env`; реальные доступы коммитить не нужно.
