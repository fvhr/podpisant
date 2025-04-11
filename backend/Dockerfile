FROM python:3.12.7-alpine

RUN apk add --no-cache make build-base

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PYTHONPATH=/usr/src

WORKDIR /podpisant.api

COPY requirements .

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r prod.txt

COPY . .

RUN chmod +x docker/serve.sh && \
    chmod +x docker/migrate.sh