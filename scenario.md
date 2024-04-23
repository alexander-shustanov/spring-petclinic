## Подготовка

### Настройка подключения к Rancher Docker из IDEA

Если используете Rancher Desktop, нужно в настройках IDEA задать путь к docker.

1. Узнать путь до docker:
   ```bash
   type docker
   ```
2. Зайти в Settings > Build, Execution, Deployment > Docker > Tools.
   В обоих полях прописать путь до docker, полученный на предыдущем шаге.
3. Узнать Docker Endpoint, выполнив команду
   ```bash
   docker context ls
   ```
   Нас интересует значение в колонке `DOCKER ENDPOINT` для контекста
   rancher-desktop.
4. Settings > Build, Execution, Deployment > Docker выбрать TCP Socket и
   указать значение для Engine API URL, полученное на предыдущем шаге.

### Очистка после предыдущих прогонов

**Перед проходом сценария обязательно удостовериться, что ничего не осталось 
  от прошлых запусков:**
* Открываем панель Services, не должно быть вообще никаких (ни запущенных, ни
  остановленных) композов. Если есть, то выключаем каждый через right-click > Down
* Чистим Volumes: right-click на Volumes > Clean Up

## Сценарии

### Локальное окружение в Docker Compose для разработки приложения

Ветка: `jpoint-demo/docker-local-dev`

*Цель: подготовить локальное окружение для разработки, которое будет содержать
 необходимые для работы приложения 3rd party сервисы*

1. Через Amplicode Explorer создаем Docker Compose файл
   (+ > Docker > Docker Compose File). 
   В открывшемся диалоге оставляем поля по-умолчанию.
   
   В корне проекта будет создан файл docker-compose.yaml и будет открыт в 
   редакторе.

2. Добавляем Postgres через Suggestions (можно использовать лампочку в editor 
   toolbar или через меню Generate CMD-N). В диалоге оставляем поля по-умолчанию.
   
   Обращаем внимание, что Amplicode заполнил параметры для сервиса в 
   соответствии с параметрами существующего в проекте Data Source.

3. Добавляем pgAdmin также через Suggestions. В открывшемся диалоге оставляем 
   поля по-умолчанию.

   После добавления сервиса в проекте создалась директория `docker/pgadmin` с
   файлами `pgpass` и `servers.json`. Они содержат креды нашего postgres сервера.

4. Говорим, что у нас есть существующий дамп базы и мы хотим, чтобы
   контейнер PostgreSQL был создан из дампа. Становимся в файле где-угодно внутри
   определения сервиса `postgres`, открываем инспектор. В инспекторе находим
   Volumes > Init Scripts. В Source нажимаем на кнопку выбора директории и находим
   `docker/postgres/dump`. 

5. Запускаем `docker-compose.yaml` (можно использовать line gutter или right-click
   на файл в Amplicode Explorer). Ждем запуска сервисов.

6. Открываем pgAdmin в браузере (через ссылку внутри редактора или right-click
   на pgAdmin в Amplicode Explorer > Open in Browser). Если вдруг ссылка в
   редакторе не отобразится, можно зайти напрямую в браузере на 
   [http://localhost:5050/](http://localhost:5050/).

7. В pdAdmin демонстрируем, что база была создана и проинициализирована из дампа.
   Обращаем внимание, что ни пользователя, ни пароля мы нигде не вводили, так как
   Amplicode сделал это за нас при генерации сервиса (см. п 3).

8. Добавляем Kafka в `docker-compose.yaml` (можно использовать палитру, 
   меню Generate, кнопку "+" в тулбаре редактора или код комплишен).

9. Используя Suggestions, добавляем Kafka UI. Amplicode автоматически обнаруживает
   ранее добавленный сервис Kafka и предлагает настроить подключение к нему.

10. Перезапускаем `docker-compose.yaml`.

11. Запускаем приложение через Run Configuration, переходим в браузере - показываем, 
    что приложение полностью работает с локальным окружением, поднятым в Docker Compose.

### Контейнеризация Spring Boot приложения

Ветка: `jpoint-demo/docker-app-depl` (или на основе предыдущего сценария)

*Цель: подготовить конфигурацию для развертывания приложения на тестовое окружение.
 В качестве тестового сервера используется виртуальная машина, на которой 
 установлен Docker.*

*Будем использовать существующий `docker-compose.yaml`, который описывает локальное
 dev окружение. Он содержит все необходимые 3rd party сервисы, но в нем не хватает
 самого приложения.*

У сценария есть 2 варианта:
* используем тот же самый файл для тестового окружения и профили
* создаем новый файл для тестового окружения и используем extends

#### Один docker-compose.yaml с профилями

1. Добавляем `Spring Boot` в `docker-compose.yaml` из палитры, меню Generate или тулбара.

2. В поле Dockerfile нам необходимо указать Dockerfile для нашего приложения, 
   у нас его пока нет, поэтому создаем. Нажимаем кнопку "+", открывается окно
   создания Dockerfile.

3. Выбираем опцию `Include application build stage`, это позволит создать
   multi-stage Dockerfile, который сначала собирает JAR с использованием Gradle,
   а затем упаковывает его в конечный образ. Нажимаем "ОК".

4. Нажимаем "ОК" для закрытия диалога добавления сервиса для Spring Boot приложения.

5. Теперь необходимо в сервис приложения передать параметры для подключения к PostgreSQL
   и Kafka. Для этого сначала надо объявить переменные окружения в самом приложении.
   
   В `application.properties` находим `spring.datasource.url`, `spring.datasource.username`,
   `spring.datasource.password`, `spring.kafka.bootstrap-servers` и вызываем для них 
   контекстное действие `Wrap property value into environment variable`.

6. Возвращаемся в `docker-compose.yaml`. В сервисе `spring-petclinic` добавляем секцию
   `environment` и с помощью код комплишена заполняем значения переменных. Значения для
   `POSTGRES_DB_NAME`, `POSTGRES_USERNAME` и `POSTGRES_PASSWORD` списываем из переменных
   сервиса `postgres`.

   ```yaml
   environment:
      POSTGRES_HOST: postgres:5432
      POSTGRES_DB_NAME: spring-petclinic
      POSTGRES_USERNAME: root
      POSTGRES_PASSWORD: root
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
   ```

7. Запускаем `docker-compose.yaml`, открываем приложение в браузере (через ссылку внутри 
   редактора или right-click на spring-petclinic в Amplicode Explorer > Open in Browser).
   Удостоверяемся, что всё работает.

8. Отмечаем, что теперь мы потеряли возможность запускать наше локальное окружение без 
   самого сервиса. Решение - использовать профили. 

   Выбираем в файле сервис `spring-petclinic`, открываем инспектор и прописываем в 
   параметре `Profiles` значение `app`. 

   Теперь, когда будем запускать файл без указания профиля, будут запускаться все 
   сервисы, кроме приложения. А при развертывании на тестовое окружение, будем запускать 
   с профилем `app` - и сервис нашего приложения также запустится.

#### Несколько docker-compose файлов с extends

1. Через Amplicode Explorer создаем Docker Compose файл (+ > Docker > Docker Compose File).
   Имя файла: `docker-compose.staging.yaml`

2. Добавляем сервис `Spring Boot` в `docker-compose.yaml` из палитры, меню Generate или 
   тулбара.

3. В поле Dockerfile нам необходимо указать Dockerfile для нашего приложения,
   у нас его пока нет, поэтому создаем. Нажимаем кнопку "+", открывается окно
   создания Dockerfile.

4. Выбираем опцию `Include application build stage`, это позволит создать
   multi-stage Dockerfile, который сначала собирает JAR с использованием Gradle,
   а затем упаковывает его в конечный образ. Нажимаем "ОК".

5. Нажимаем "ОК" для закрытия диалога добавления сервиса для Spring Boot приложения.

6. Теперь нужно добавить сервисы для PostgreSQL и Kafka. Мы могли бы их добавить заново,
   но ведь они уже добавлены в `docker-compose.yaml` и будет плохой практикой содержать
   несколько одинаковых конфигураций одних и тех же сервисов. Поэтому мы можем 
   воспользоваться существующим в Docker Compose механизмом extends.

   В файле `docker-compose.staging.yaml` вызываем действие `Extend existing service` из
   меню Generate (CMD-N). 

   Выбираем в выпадающем списке `postgres` из `docker-compose.yaml` и нажимаем "ОК".
   Аналогично добавляем `kafka`. 

7. Теперь необходимо в сервис приложения передать параметры для подключения к PostgreSQL
   и Kafka. Для этого сначала надо объявить переменные окружения в самом приложении.

   В `application.properties` находим `spring.datasource.url`, `spring.datasource.username`,
   `spring.datasource.password`, `spring.kafka.bootstrap-servers` и вызываем для них
   контекстное действие `Wrap property value into environment variable`.

8. Возвращаемся в `docker-compose.yaml`. В сервисе `spring-petclinic` добавляем секцию
   `environment` и с помощью код комплишена заполняем значения переменных.
   
   Значения для `POSTGRES_DB_NAME`, `POSTGRES_USERNAME` и `POSTGRES_PASSWORD` обычно мы 
   бы списали из переменных сервиса `postgres`, но из-за использования `extends` они 
   остались в файле `docker-compose.yaml`. Мы можем постоянно переключаться туда, а можем
   открыть инспектор и посмотреть значения в нём. (Опционально, можно изменить какое-нибудь 
   значение, например, для POSTGRES_USERNAME)

   Заполняем параметры:

   ```yaml
   environment:
      POSTGRES_HOST: postgres:5432
      POSTGRES_DB_NAME: spring-petclinic
      POSTGRES_USERNAME: root
      POSTGRES_PASSWORD: root
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
   ```
   
9. Запускаем `docker-compose.staging.yaml`, открываем приложение в браузере (через ссылку 
   внутри редактора или right-click на spring-petclinic в Amplicode Explorer > Open in 
   Browser). Удостоверяемся, что всё работает.

   Таким образом, файл `docker-compose.yaml` мы можем использовать для запуска локального
   дев окружения, а для запуска на staging - `docker-compose.staging.yaml`, при этом
   конфигурация для сервисов переиспользуется между файлами.
