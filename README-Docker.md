# Docker Setup для Sutod проекта

Этот проект содержит Docker конфигурацию для запуска всего стека приложений.

## Архитектура

- **Frontend**: React приложение на порту 3000
- **Backend**: Spring Boot приложение на порту 8080
- **Database**: PostgreSQL на порту 5432
- **Cache**: Redis на порту 6379 (опционально)

## Требования

- Docker
- Docker Compose

## Быстрый старт

### 1. Запуск всего стека

```bash
# Клонировать репозиторий
git clone <repository-url>
cd Sutod

# Запустить все сервисы
docker-compose up -d

# Проверить статус
docker-compose ps
```

### 2. Доступ к приложениям

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432
- **Redis**: localhost:6379

## Отдельная сборка образов

### Spring Boot Backend

```bash
cd Sutod_Auth
docker build -t sutod-backend .
docker run -p 8080:8080 sutod-backend
```

### React Frontend

```bash
cd Frontend
docker build -t sutod-frontend .
docker run -p 3000:80 sutod-frontend
```

## Переменные окружения

### Backend (.env файл)

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/SutodApp
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=1928
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

### Frontend (.env файл)

```bash
REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=ws://localhost:8080/ws
```

## Полезные команды

```bash
# Просмотр логов
docker-compose logs -f [service-name]

# Остановка сервисов
docker-compose down

# Пересборка и перезапуск
docker-compose up --build

# Очистка
docker-compose down -v
docker system prune -f

# Проверка здоровья сервисов
docker-compose ps
```

## Структура файлов

```
Sutod/
├── docker-compose.yml          # Основной compose файл
├── Sutod_Auth/
│   ├── Dockerfile             # Dockerfile для Spring Boot
│   └── .dockerignore          # Исключения для Docker
├── Frontend/
│   ├── Dockerfile             # Dockerfile для React
│   ├── nginx.conf             # Конфигурация nginx
│   └── .dockerignore          # Исключения для Docker
└── README-Docker.md           # Этот файл
```

## Особенности конфигурации

### Spring Boot Backend
- Многоэтапная сборка с Maven
- Java 24 (Eclipse Temurin)
- Alpine Linux для минимального размера
- Health check через actuator
- Непривилегированный пользователь

### React Frontend
- Многоэтапная сборка с Node.js
- Nginx для раздачи статики
- Поддержка React Router
- Gzip сжатие
- Безопасность через заголовки
- Кэширование статических ресурсов

### PostgreSQL
- Автоматическое применение миграций Flyway
- Health check
- Персистентные данные

## Troubleshooting

### Проблемы с подключением к базе данных
```bash
# Проверить статус PostgreSQL
docker-compose logs postgres

# Подключиться к базе
docker-compose exec postgres psql -U postgres -d SutodApp
```

### Проблемы с Frontend
```bash
# Проверить логи nginx
docker-compose logs frontend

# Проверить конфигурацию
docker-compose exec frontend nginx -t
```

### Проблемы с Backend
```bash
# Проверить логи Spring Boot
docker-compose logs backend

# Проверить health check
curl http://localhost:8080/actuator/health
```

## Production рекомендации

1. **Безопасность**: Измените пароли по умолчанию
2. **SSL**: Добавьте SSL сертификаты
3. **Мониторинг**: Интегрируйте с системами мониторинга
4. **Backup**: Настройте регулярное резервное копирование
5. **Scaling**: Используйте Docker Swarm или Kubernetes для масштабирования

## Лицензия

Этот проект использует те же лицензии, что и основной проект Sutod.

