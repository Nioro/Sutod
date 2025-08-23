.PHONY: help build up down restart logs clean ps shell-backend shell-frontend shell-db

# Default target
help:
	@echo "Доступные команды:"
	@echo "  build        - Собрать все образы"
	@echo "  up           - Запустить все сервисы"
	@echo "  down         - Остановить все сервисы"
	@echo "  restart      - Перезапустить все сервисы"
	@echo "  logs         - Показать логи всех сервисов"
	@echo "  logs-backend - Показать логи backend"
	@echo "  logs-frontend- Показать логи frontend"
	@echo "  logs-db      - Показать логи базы данных"
	@echo "  clean        - Очистить все контейнеры и образы"
	@echo "  ps           - Показать статус сервисов"
	@echo "  shell-backend- Подключиться к backend контейнеру"
	@echo "  shell-frontend- Подключиться к frontend контейнеру"
	@echo "  shell-db     - Подключиться к базе данных"
	@echo "  health       - Проверить здоровье сервисов"

# Build all images
build:
	docker-compose build

# Start all services
up:
	docker-compose up -d

# Stop all services
down:
	docker-compose down

# Restart all services
restart:
	docker-compose restart

# Show logs for all services
logs:
	docker-compose logs -f

# Show logs for specific service
logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend

logs-db:
	docker-compose logs -f postgres

# Clean up containers and images
clean:
	docker-compose down -v
	docker system prune -f
	docker image prune -f

# Show service status
ps:
	docker-compose ps

# Shell into containers
shell-backend:
	docker-compose exec backend sh

shell-frontend:
	docker-compose exec frontend sh

shell-db:
	docker-compose exec postgres psql -U postgres -d SutodApp

# Health check
health:
	@echo "Проверка здоровья сервисов..."
	@echo "Backend:"
	@curl -s http://localhost:8080/actuator/health || echo "Backend недоступен"
	@echo "Frontend:"
	@curl -s http://localhost:3000/health || echo "Frontend недоступен"
	@echo "Database:"
	@docker-compose exec -T postgres pg_isready -U postgres -d SutodApp || echo "Database недоступна"

# Development commands
dev-up:
	docker-compose -f docker-compose.yml up -d postgres redis

dev-down:
	docker-compose -f docker-compose.yml down postgres redis

# Production commands
prod-up:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

prod-down:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml down

# Backup database
backup:
	@echo "Создание резервной копии базы данных..."
	docker-compose exec -T postgres pg_dump -U postgres SutodApp > backup_$(shell date +%Y%m%d_%H%M%S).sql

# Restore database
restore:
	@echo "Восстановление базы данных из файла..."
	@read -p "Введите имя файла для восстановления: " file; \
	docker-compose exec -T postgres psql -U postgres -d SutodApp < $$file


