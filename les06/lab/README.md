# Лабораторная работа № 6 — JDBC и встраиваемая БД H2

## Цель

Подключить к приложению магазина зоотоваров встраиваемую СУБД H2 через `EmbeddedDatabaseBuilder`, описать таблицы `CATEGORIES` и `PRODUCTS` с внешним ключом, загрузить данные из CSV в БД (`DataBaseRenderer`, `JdbcTemplate`) и выполнить выборку категорий с числом товаров больше одного с выводом результата в консоль через Logback (уровень `INFO`).

## Запуск

Из каталога `les06/lab`:

```bash
./gradlew run
```

или

```bash
gradle run
```

Корневой проект делегирует задачу `:app:run`. Альтернатива: `./gradlew :app:run`.

## Структура

- `app/src/main/resources/schema.sql` — создание таблиц `CATEGORIES`, `PRODUCTS` и ограничение `FOREIGN KEY`.
- `app/src/main/resources/category.csv`, `product.csv` — исходные данные.
- `LabConfiguration` — бин `DataSource` (`EmbeddedDatabaseBuilder` + H2 + выполнение `schema.sql`), `JdbcTemplate`, провайдеры CSV, рендерер по умолчанию `DataBaseRenderer`, `CategoryRequest`.
- `CategoryRequest` — SQL-запрос с `GROUP BY` / `HAVING COUNT(...) > 1` и `RowMapper` для разбора строк результата.

## UML-диаграмма классов (Mermaid)

```mermaid
classDiagram
    direction TB

    class App {
        +main(String[] args)
    }

    class LabConfiguration {
        +dataSource() DataSource
        +jdbcTemplate(DataSource) JdbcTemplate
        +productReader() Reader
        +categoryReader() Reader
        +parser() Parser
        +categoryParser() CategoryParser
        +productProvider(Reader, Parser) ProductProvider
        +categoryProvider(Reader, CategoryParser) CategoryProvider
        +renderer(...) Renderer
        +categoryRequest(JdbcTemplate) CategoryRequest
    }

    class Reader {
        <<interface>>
        +read() String
    }

    class Parser {
        <<interface>>
        +parse(String) List~Product~
    }

    class CategoryParser {
        <<interface>>
        +parse(String) List~Category~
    }

    class ProductProvider {
        <<interface>>
        +getProducts() List~Product~
    }

    class CategoryProvider {
        <<interface>>
        +getCategories() List~Category~
    }

    class Renderer {
        <<interface>>
        +render() void
    }

    class Product {
        -long productId
        -String name
        ...
    }

    class Category {
        -long categoryId
        -String name
        -String description
    }

    class CategoryProductSummary {
        <<record>>
        categoryId
        name
        description
        productCount
    }

    class CategoryRequest {
        -JdbcTemplate jdbcTemplate
        +execute() void
    }

    class ResourceFileReader {
        +read() String
    }

    class CSVParser {
        +parse(String) List~Product~
        +splitCsvLine(String)$ List~String~
    }

    class CategoryCSVParser {
        +parse(String) List~Category~
    }

    class ConcreteProductProvider {
        +getProducts() List~Product~
    }

    class ConcreteCategoryProvider {
        +getCategories() List~Category~
    }

    class ConsoleTableRenderer {
        +render() void
    }

    class DataBaseRenderer {
        -JdbcTemplate jdbcTemplate
        +render() void
    }

    class CategoryMultiProductRowMapper {
        <<Spring RowMapper>>
        +mapRow(ResultSet, int) CategoryProductSummary
    }

    App --> LabConfiguration : AnnotationConfigApplicationContext
    App ..> Renderer : getBean
    App ..> CategoryRequest : getBean

    LabConfiguration ..> Reader : @Bean
    LabConfiguration ..> Parser : @Bean
    LabConfiguration ..> CategoryParser : @Bean
    LabConfiguration ..> ProductProvider : @Bean
    LabConfiguration ..> CategoryProvider : @Bean
    LabConfiguration ..> Renderer : @Bean renderer
    LabConfiguration ..> CategoryRequest : @Bean

    ResourceFileReader ..|> Reader
    CSVParser ..|> Parser
    CategoryCSVParser ..|> CategoryParser

    ConcreteProductProvider ..|> ProductProvider
    ConcreteCategoryProvider ..|> CategoryProvider

    ConcreteProductProvider --> Reader
    ConcreteProductProvider --> Parser
    ConcreteCategoryProvider --> Reader
    ConcreteCategoryProvider --> CategoryParser

    ConsoleTableRenderer ..|> Renderer
    DataBaseRenderer ..|> Renderer

    ConsoleTableRenderer --> ProductProvider
    DataBaseRenderer --> JdbcTemplate
    DataBaseRenderer --> ProductProvider
    DataBaseRenderer --> CategoryProvider

    CategoryRequest --> JdbcTemplate
    CategoryRequest ..> CategoryMultiProductRowMapper
    CategoryMultiProductRowMapper ..> CategoryProductSummary : creates

    ProductProvider ..> Product : supplies
    CategoryProvider ..> Category : supplies
```

## Ожидаемый вывод

После загрузки данных в БД в логе уровня `INFO` отображается сводка от `DataBaseRenderer`, затем — результат запроса `CategoryRequest`: категории, в которых больше одного товара (для текущих CSV это категория «Средства ухода», `category_id = 5`, два товара).
