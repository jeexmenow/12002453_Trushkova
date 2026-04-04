# Отчёт: лабораторная работа 2 (les02/lab)

## Кратко: что такое Gradle

**Gradle** — это **система автоматической сборки** для JVM-проектов (и не только). Она по описанию проекта (файлы `build.gradle.kts`, `settings.gradle.kts`, каталог `gradle/`) умеет:

- скачивать **зависимости** из Maven Central и других репозиториев;
- **компилировать** Java-код и ресурсы;
- запускать **тесты**;
- собирать **JAR**, дистрибутивы приложения;
- предоставлять готовые **задачи** вроде `run`, `build`, `test`.

Команда **`gradle`** — это установленный в системе Gradle. В проекте обычно используют **Gradle Wrapper** (`gradlew` / `gradlew.bat`): он подтягивает нужную версию Gradle, чтобы у всех разработчиков сборка была одинаковой. Подробнее в [руководстве по установке Gradle](https://docs.gradle.org/current/userguide/installation.html) и [примере Java Application](https://docs.gradle.org/current/samples/sample_building_java_applications.html).

**Зависимость** `org.springframework:spring-context:6.2.2` подключает **контейнер Spring**: конфигурацию бинов, внедрение зависимостей и жизненный цикл компонентов без ручного `new` всего дерева объектов.

## Цель работы

Создать консольное приложение на **Spring** с **Java-конфигурацией**, прочитать товары из **CSV** в `src/main/resources`, вывести **таблицу** в консоль, запуск через **`gradle run`**.

## Выполнение

### Структура проекта

- Корень Gradle: `les02/lab` (имя сборки **product-table**, подпроект **`app`**).
- Исходный код: `app/src/main/java/ru/bsuedu/cad/lab/`.
- Данные: `app/src/main/resources/product.csv` (скопирован из `les02/assets/product.csv` по заданию).

### Диаграмма классов (реализация)

| Роль | Интерфейс | Реализация |
|------|-----------|------------|
| Чтение файла | `Reader` | `ResourceFileReader` — читает ресурс с classpath |
| Разбор CSV | `Parser` | `CSVParser` — строки в `List<Product>` (учёт кавычек и запятых в полях) |
| Список товаров | `ProductProvider` | `ConcreteProductProvider` — `reader` + `parser` |
| Вывод | `Renderer` | `ConsoleTableRenderer` — таблица с рамкой `+---+` / `\|` |
| Сущность | `Product` | поля как в методичке: id, name, description, categoryId, price, stock, imageUrl, createdAt, updatedAt |

Реализации расположены в пакете `ru.bsuedu.cad.lab.impl`.

### Spring (Java-конфигурация)

Класс `LabConfiguration` помечен `@Configuration`. Методы с `@Bean` создают компоненты; зависимости передаются **параметрами методов** (внедрение через конструктор при вызове `@Bean`-методов Spring’ом). Запуск как в лекции: `AnnotationConfigApplicationContext(LabConfiguration.class)`.

Точка входа: `ru.bsuedu.cad.lab.App` — получает бин `renderer` и вызывает `render()`.

### Запуск

Из каталога `les02/lab`:

```bash
.\gradlew.bat run
```

или (если в PATH есть Gradle 8.12+ и настроены `JAVA_HOME` / JDK 17):

```bash
gradle run
```

Сборка и тесты:

```bash
.\gradlew.bat build
```

Для корректного отображения **кириллицы** в стандартной консоли Windows может понадобиться UTF-8, например **`chcp 65001`** перед запуском или терминал вроде **Windows Terminal** с UTF-8.

## Выводы

Освоена базовая работа с **Gradle** (wrapper, задачи `run`/`build`/`test`), подключён **Spring Context** для сборки графа объектов по **Java-config**. Данные отделены от представления: чтение и разбор CSV инкапсулированы в `Reader`/`Parser`, вывод — в `Renderer`, что соответствует слабой связности компонентов.
