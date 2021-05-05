FROM postgres:latest
LABEL maintainer="alexmaltsev111@gmail.com"

ENV POSTGRES_PASSWORD=jumanji
ENV POSTGRES_DB=onlinechat

COPY ./dbschema.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 5432


