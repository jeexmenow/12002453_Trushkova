# Лабораторная работа 1 (семестр 2). Введение в Spring Boot

Трушкова А.И., группа 12002453

## Цель

Получить практические навыки работы со Spring Boot: собрать веб-приложение и описать REST-методы для хранения заметок.

## Что сделано

Проект лежит в `app` и собран по примеру из методички (Spring Boot 3.2.3, Java 21, Maven, `spring-boot-starter-web`).

Класс `DemoApplication` запускает приложение. Класс `MessageController` хранит заметки в памяти и обрабатывает запросы:

| Метод | Адрес | Действие |
|---|---|---|
| GET | `/` | Приветствие `Hello, World!` |
| GET | `/messages` | Список заметок |
| POST | `/messages` | Публикация заметки |
| PUT | `/messages/{index}` | Изменение текста заметки |
| DELETE | `/messages/{index}` | Удаление заметки |

У каждой заметки есть текст и время публикации. Время выставляет сервер в момент публикации и не меняет его при редактировании.

## Дополнительное задание

В методичке шесть вариантов, варианта 18 нет. Номер 18 приведён к шестому пункту списка: `(18 - 1) mod 6 + 1 = 6`.

Задание при этом просит два дополнительных метода, поэтому сделаны два:

1. Вариант 6. `countMessages()` — `GET /messages/count`, возвращает число заметок.
2. Вариант 5. `getMessagesAfter(from)` — `GET /messages/after?from=2026-10-04T14:00:00`, возвращает заметки, опубликованные строго позже указанной даты и времени.

## Запуск

Нужны JDK 21 и Maven. Из каталога `app`:

```bash
mvn spring-boot:run
```

Проверка:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/"

Invoke-RestMethod -Uri "http://localhost:8080/messages" -Method Post -Body '"Первая заметка"' -ContentType "application/json; charset=utf-8"

Invoke-RestMethod -Uri "http://localhost:8080/messages"

Invoke-RestMethod -Uri "http://localhost:8080/messages/count"

Invoke-RestMethod -Uri "http://localhost:8080/messages/after?from=2020-01-01T00:00:00"
```

Тело POST и PUT передаётся JSON-строкой, то есть в кавычках: `"текст"`.

## Проверка

Автотест `MessageControllerTest` проходит публикацию, подсчёт, фильтр по дате, изменение и удаление, а также запрос к несуществующему индексу.

```bash
mvn test
```
