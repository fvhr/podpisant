FROM python:3.12.7-alpine

RUN mkdir /notification_app

WORKDIR /notification_app

ENV PYTHONDONTWRITEBYTECODE 1

ENV PYTHONUNBUFFERED 1

ENV PYTHONPATH=/usr/src

COPY poetry.lock pyproject.toml ./

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir poetry && \
    poetry config virtualenvs.create false && \
    poetry install --no-root

COPY / .

RUN chmod +x /notification_app/docker/app.sh