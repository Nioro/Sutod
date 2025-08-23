#!/bin/bash

# Скрипт для инициализации production окружения
# Создает необходимые директории и файлы

set -e

echo "🚀 Инициализация production окружения для Sutod проекта..."

# Создание директорий
echo "📁 Создание директорий..."
mkdir -p secrets
mkdir -p nginx/ssl
mkdir -p nginx/logs
mkdir -p backups

# Создание файлов секретов (если не существуют)
echo "🔐 Создание файлов секретов..."

# Database
if [ ! -f "secrets/db_password.txt" ]; then
    echo "Введите пароль для базы данных:"
    read -s db_password
    echo "$db_password" > secrets/db_password.txt
    echo "✅ Пароль для базы данных сохранен"
fi

if [ ! -f "secrets/db_user.txt" ]; then
    echo "postgres" > secrets/db_user.txt
    echo "✅ Пользователь базы данных сохранен"
fi

if [ ! -f "secrets/db_name.txt" ]; then
    echo "SutodApp" > secrets/db_name.txt
    echo "✅ Имя базы данных сохранено"
fi

# Email
if [ ! -f "secrets/mail_username.txt" ]; then
    echo "Введите email для SMTP:"
    read mail_username
    echo "$mail_username" > secrets/mail_username.txt
    echo "✅ Email для SMTP сохранен"
fi

if [ ! -f "secrets/mail_password.txt" ]; then
    echo "Введите пароль приложения для SMTP:"
    read -s mail_password
    echo "$mail_password" > secrets/mail_password.txt
    echo "✅ Пароль SMTP сохранен"
fi

# JWT
if [ ! -f "secrets/jwt_secret.txt" ]; then
    jwt_secret=$(openssl rand -base64 64)
    echo "$jwt_secret" > secrets/jwt_secret.txt
    echo "✅ JWT секрет сгенерирован"
fi

# Redis
if [ ! -f "secrets/redis_password.txt" ]; then
    redis_password=$(openssl rand -base64 32)
    echo "$redis_password" > secrets/redis_password.txt
    echo "✅ Redis пароль сгенерирован"
fi

# Установка прав доступа
echo "🔒 Установка прав доступа..."
chmod 600 secrets/*.txt
chmod 755 secrets
chmod 755 nginx
chmod 755 backups

# Создание production nginx конфигурации
echo "🌐 Создание production nginx конфигурации..."
cat > nginx/nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log warn;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    # Upstream servers
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:80;
    }

    # HTTP redirect to HTTPS
    server {
        listen 80;
        server_name _;
        return 301 https://$host$request_uri;
    }

    # HTTPS server
    server {
        listen 443 ssl http2;
        server_name _;

        # SSL configuration
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 10m;

        # Frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Backend API
        location /api/ {
            limit_req zone=api burst=20 nodelay;
            proxy_pass http://backend/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # WebSocket support
        location /ws {
            proxy_pass http://backend/ws;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
EOF

echo "✅ Production nginx конфигурация создана"

# Создание self-signed SSL сертификата (для тестирования)
echo "🔒 Создание self-signed SSL сертификата..."
if [ ! -f "nginx/ssl/cert.pem" ] || [ ! -f "nginx/ssl/key.pem" ]; then
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout nginx/ssl/key.pem \
        -out nginx/ssl/cert.pem \
        -subj "/C=RU/ST=State/L=City/O=Organization/CN=localhost"
    echo "✅ Self-signed SSL сертификат создан"
else
    echo "ℹ️  SSL сертификаты уже существуют"
fi

# Создание .env файла для production
echo "📝 Создание .env файла для production..."
cat > .env.production << EOF
# Production Environment Variables
NODE_ENV=production
SPRING_PROFILES_ACTIVE=production

# Database
POSTGRES_DB=SutodApp
POSTGRES_USER=postgres
POSTGRES_PASSWORD=\$(cat secrets/db_password.txt)
POSTGRES_HOST=postgres
POSTGRES_PORT=5432

# Spring Boot
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/SutodApp
SPRING_DATASOURCE_USERNAME=\$(cat secrets/db_user.txt)
SPRING_DATASOURCE_PASSWORD=\$(cat secrets/db_password.txt)

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=\$(cat secrets/mail_username.txt)
SPRING_MAIL_PASSWORD=\$(cat secrets/mail_password.txt)

# JWT
JWT_SECRET=\$(cat secrets/jwt_secret.txt)
JWT_EXPIRATION=86400000

# Redis
REDIS_PASSWORD=\$(cat secrets/redis_password.txt)
REDIS_HOST=redis
REDIS_PORT=6379

# Frontend
REACT_APP_API_URL=https://localhost/api
REACT_APP_WS_URL=wss://localhost/ws
EOF

echo "✅ .env файл для production создан"

# Создание скрипта для backup
echo "💾 Создание скрипта для backup..."
cat > backup.sh << 'EOF'
#!/bin/bash

# Скрипт для создания резервной копии базы данных
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/backup_$DATE.sql"

echo "🔄 Создание резервной копии базы данных..."
docker-compose exec -T postgres pg_dump -U postgres SutodApp > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "✅ Резервная копия создана: $BACKUP_FILE"
    
    # Удаление старых backup файлов (оставляем последние 10)
    cd "$BACKUP_DIR"
    ls -t backup_*.sql | tail -n +11 | xargs -r rm
    echo "🧹 Старые backup файлы удалены"
else
    echo "❌ Ошибка при создании резервной копии"
    exit 1
fi
EOF

chmod +x backup.sh
echo "✅ Скрипт для backup создан"

# Создание скрипта для мониторинга
echo "📊 Создание скрипта для мониторинга..."
cat > monitor.sh << 'EOF'
#!/bin/bash

# Скрипт для мониторинга сервисов
echo "🔍 Мониторинг сервисов Sutod..."

echo "📊 Статус контейнеров:"
docker-compose ps

echo ""
echo "💾 Использование диска:"
docker system df

echo ""
echo "🧠 Использование памяти:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}"

echo ""
echo "📈 Логи последних ошибок:"
docker-compose logs --tail=10 | grep -i error || echo "Ошибок не найдено"
EOF

chmod +x monitor.sh
echo "✅ Скрипт для мониторинга создан"

echo ""
echo "🎉 Production окружение успешно инициализировано!"
echo ""
echo "📋 Следующие шаги:"
echo "1. Проверьте и отредактируйте файлы в директории secrets/"
echo "2. Замените self-signed SSL сертификат на реальный"
echo "3. Запустите production окружение: docker-compose -f docker-compose.prod.yml up -d"
echo "4. Проверьте логи: docker-compose -f docker-compose.prod.yml logs -f"
echo ""
echo "📚 Дополнительная информация:"
echo "- README-Docker.md - документация по Docker"
echo "- Makefile - удобные команды для управления"
echo "- backup.sh - создание резервных копий"
echo "- monitor.sh - мониторинг сервисов"


