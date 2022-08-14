FROM public.ecr.aws/docker/library/mariadb:latest

COPY ./database_script.sql /docker-entrypoint-initdb.d/10.sql
COPY ./initial_user_setup.sql /docker-entrypoint-initdb.d/20.sql
COPY ./utinni_sample_decks.sql /docker-entrypoint-initdb.d/30.sql
