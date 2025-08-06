# Search Engine

Простой поисковый движок, реализующий индексирование сайтов, морфологический разбор и полнотекстовый поиск с подсветкой сниппетов.

## Возможности

- Полнотекстовый поиск с лемматизацией
- Генерация сниппетов с контекстом запроса
- Индексация сайтов и сбор статистики
- REST API для поиска и статистики
- Веб-интерфейс на Thymeleaf
- Spring Boot (2.7.1) + MySQL

## Зависимости

Все зависимости указаны в `pom.xml`.

Дополнительные **зависимости для морфологии** (лемматизации) нужно загрузить вручную:

📥 [Скачать зависимости морфологии с Яндекс.Диска](https://disk.yandex.ru/d/u3_KHfNHVKVMiw)


## Тестирование

Проект содержит:

- Unit-тесты (`SnippetFormatterTest`)
- Интеграционные тесты:
    - `SearchServiceImplTest`
    - `StatisticsServiceImplTest`
    - `SiteRepositoryTest`
    - `SearchControllerTest` (через `MockMvc`)

Отчёты о покрытии кода генерируются через **JaCoCo**:

```
mvn clean test
```

```Сборка Докера
mvn clean package -DskipTests
docker build -t search_engine .
```
```Запуск Docker
docker run -d -p 8080:8080 search_engine
```

                                                            
                                                           Архитектура проекта: 
![img.png](img.png)