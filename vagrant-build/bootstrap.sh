#!/usr/bin/env bash

export DEBIAN_FRONTEND=noninteractive

echo
echo "Install dependencies"
echo
echo "  * apt-get update"
apt-get update
echo "  * apt-get install wget unzip vim-common dos2unix git htop"
apt-get install -y wget unzip vim-common dos2unix git htop
echo "  * apt-get install mariadb-client mariadb-server"
apt-get install -y mariadb-client mariadb-server
echo "  * apt-get install openjdk-11-jdk maven"
apt-get install -y openjdk-11-jdk maven

echo
echo "Set up GEMP directory structure"
echo "  * mkdir -p /env/gemp-swccg/web"
mkdir -p /env/gemp-swccg/web
echo "  * chown /env/gemp-swccg"
chown -R vagrant:vagrant /env/gemp-swccg
echo "  * mkdir /logs"
mkdir /logs/
echo "  * chown /logs"
chown vagrant:vagrant /logs

echo
echo "Setting CWD To /vagrant in bash_profile"
echo
echo "cd /vagrant" >> ~vagrant/.bash_profile


echo
echo "Fix windows line endings if necessary at login"
echo
echo 'dos2unix /vagrant/get-card-images.sh 1>/dev/null 2>&1' >> ~vagrant/.bash_profile
echo 'dos2unix /vagrant/run-gemp.sh 1>/dev/null 2>&1' >> ~vagrant/.bash_profile
echo
source ~vagrant/.bash_profile

echo
echo "Configure MariaDB"
echo "  * set listening host to 0.0.0.0"
sed -i 's/127.0.0.1/0.0.0.0/g' /etc/mysql/mariadb.conf.d/50-server.cnf
echo "  * enable MariaDB service"
systemctl enable --now mariadb.service

echo "  * Create Database user"
mysql -u root mysql <<< "CREATE USER 'gemp'@'localhost' IDENTIFIED BY 'gemp';"
mysql -u root mysql <<< "GRANT ALL PRIVILEGES ON *.* TO 'gemp'@'localhost' WITH GRANT OPTION;"
echo "  * Load seed database"
mysql -u root mysql < /vagrant/database_script.sql
echo "  * Create test users"
mysql -u root gemp-swccg < /vagrant/initial_users.sql
echo "  * Load sample decks"
mysql -u root gemp-swccg < /vagrant/sample_decks.sql


echo
echo 'Done!'
echo
