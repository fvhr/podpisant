ifeq ($(OS),Windows_NT)
    OS := windows
else
    OS := $(shell uname -s | tr A-Z a-z)
endif

check_docker:
ifeq ($(OS),windows)
	@docker ps > NUL 2>&1 || (echo "Docker is not running. Please start Docker and try again." && exit 1)
else
	@docker ps > /dev/null 2>&1 || (echo "Docker is not running. Please start Docker and try again." && exit 1)
endif

up:
	docker-compose up

up-prod:
	docker-compose --profile grafana up

up-setup:
	docker-compose --profile setup up

up-m: check_docker
	docker-compose --profile migration up -d

build: check_docker
	docker-compose build

down:
	docker-compose down

up-non-log: check_docker
	docker-compose -f ./docker-compose-non-log.yml up

down-non-log: check_docker
	docker-compose -f ./docker-compose-non-log.yml down

up-dev: check_docker
	docker-compose -f ./docker-compose-local-dev.yml up --build

down-dev: check_docker
	docker-compose -f ./docker-compose-local-dev.yml down

deps:
	poetry install

dbo:
	cd auth_service && \
	alembic revision --autogenerate && \
#	alembic upgrade head


downgrade:
	cd auth_service && \
	alembic downgrade -1

create_venv:
	@python -m venv venv
	@.\venv\Scripts\activate.bat

install_requirements:
	@pip install -r requirements/prod.txt

make_migrations:
	@cd app && alembic revision --autogenerate
	@cd app && alembic upgrade head

run_project:
	@cd app && python main.py

server-prod:
	cd app && \
	uvicorn main:app --reload

nfy:
	cd notification/ && \
	python app.py

clean:
	poetry env remove python
	find . -type d -name "__pycache__" -exec rm -rf {} +
