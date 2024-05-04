FROM public.ecr.aws/docker/library/mariadb:11 AS MariaDB

COPY ../db-scripts/database_creation_script.sql /docker-entrypoint-initdb.d/10.sql
COPY ../db-scripts/initial_user_setup.sql /docker-entrypoint-initdb.d/20.sql
COPY ../db-scripts/sample_decks.sql /docker-entrypoint-initdb.d/30.sql
COPY ../db-scripts/utinni_sample_decks.sql /docker-entrypoint-initdb.d/40.sql
