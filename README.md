TeamHelper
=========================
Внутренний сервис для обработки заказов, которые поступают из корпоративной среды Slack, организует
их хранение и позволяет управлять жизненным циклом каждого заказа. В приложении есть возможность
добавления расписания, в которые могут быть определены заказы на выполнение. Также сервис позволяет
информировать заказчиков о выполнении заказа или о форс-мажорных ситуациях и команды исполнителей о
списке клиентов на конкретное событие с помощью взаимодействия оператора и приложения.

Цель
=========================
<p>Сервис должен быть доступен в браузере через интернет.
<p>Сервис должен иметь / уметь:

* Регистрировать / Авторизовывать пользователей.
* Администрировать пользователей и их роли. (Подтверждение / отклонение заявки на регистрацию. Удаление пользователей.
  Назначение ролей и привилегий пользователям)
* Принимать сообщения из корпоративной среды Slack.
* Узнавать дополнительную информацию о клиенте с помощью Blizzard Api.
* Хранить информацию о заказах.
* Создавать расписание событий, в которые можно определить клиентов.
* Графический интерфейс

Технологический стек
=========================

* Java 11
* Spring boot
* Liquibase
* Docker
* Hibernate
* Postgresql
* Redis
* Spring Security
* JWT
* OAuth2
* Spring mail
* Bolt-servlets
* AngularJS
* JS
* HTML5
* CSS
* WebSocket

Запуск приложения
=========================
<p>Перед запуском приложения необходимо интегрироваться с 2-мя внешними сервисами: Slack API и Blizzard API

Интеграция с Blizzard
==================
На данный момент она полностью встроена в приложение, поэтому дополнительных действий не требуется.

Интеграция со Slack
=========================

1. Создать новое Slack приложение

- Зайдите в Slack API [App Dashboard](https://api.slack.com/) и нажмите на "Create New App".
- Задайте имя и выберите рабочее пространство для приложения. Если у вас нет рабочего простанстава вы можете создать
  [тут](https://slack.com/get-started#/createnew)
- Нажмите "Create App"

2. Настроить разрешения для приложения

- На странице приложения выберите "OAuth & Permissions" в меню слева.
- <p>Добавьте необходимые разрешения для вашего приложения, в частности :

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`channels:history`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`channels:read`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`chat:write`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`groups:history`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`groups:read`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`im:read`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`mpim:read`**


- Нажмите "Install App to Workspace" и следуйте инструкциям, чтобы установить приложение в рабочем пространстве.

3. Получить API ключ и секретный ключ

- На старнице приложения выберите "OAuth & Permissions" в меню слева и скопируйте значение Bot User OAuth Token
- На странице приложения выберите "Basic Information" в меню слева и скопируйте значение Signing Secret.
- Полученые данные занесите в переменные окружения

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SLACK_BOT_TOKEN: **`${YOUR_TOKEN}`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SLACK_SIGNING_SECRET: **`${YOUR_SIGNING_SECRET}`**

4. Настроить вебхук

- На странице приложения выберите "Event Subscriptions" в меню слева.
- Проверьте что вы подписаны на события указанные ниже. Если вы не подписаны - добавьте их вручную.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`message.channels`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`message.groups`**

- Далее вам необходимо указать общедоступный URL-адрес приложения, сделать это можно
  через [ngrok](https://dashboard.ngrok.com/get-started/setup)

- После скачивания [ngrok](https://dashboard.ngrok.com/get-started/setup) и регистрации запустите его из командной
  строки

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`ngrok http 3100`**

- Скопируйте строчку **`https://$your-url.eu.ngrok.io`** и вставьте в поле Request URL на странице Event Subscriptions, добавьте
  энд-поинт slack/events

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`https://$your-url.eu.ngrok.io/slack/events`**

- Нажмите "Save Changes". Обратите внимание! Ваше приложение, на момент включения подписки на события, должно быть 
 запущено, в противном случае вы получете ошибку и не сможете сохранить изменения.


Запуск приложения
====================
Скомпилируйте приложение и запустите JAR файл.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`mvn package`**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`java -jar target/WowVendorTeamHelper-0.0.1-SNAPSHOT.jar`**
  
Запуск дополнительных инструментов.
=====================
Для работы приложения требуется [Docker](https://www.docker.com/products/docker-desktop/).
В командной строке запустите контейнер. (Вы должны находиться в директории приложения так как файл docker-compose.yaml находится там)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**`docker-compose up`**
   
Контейнер содержит в себе:
* PostgreSql;
* Redis;

Основные возможности представлены в виде gif изображений.
=============================
### Регистрация
![Регистрация](images/Регистрация.gif)
### Авторизация
![Авторизация](images/Авторизация.gif)
### Расписание 
![Расписание](images/Schedule.gif)
### Формирование заказов
![Заказы](images/Clients.gif)
