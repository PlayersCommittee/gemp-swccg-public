#!/usr/bin/env bash

# -----------------------------------------
#   Install dependencies
# -----------------------------------------

sudo yum -y install unzip vim dos2unix git wget
sudo yum -y install mysql mariadb-server
sudo yum -y install java-1.8.0-openjdk-devel

# Install maven
cd /usr/local
sudo wget http://www-eu.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz
sudo tar xzf apache-maven-3.5.4-bin.tar.gz
sudo ln -s apache-maven-3.5.4 maven
sudo cp /vagrant/vagrant-build/maven.sh /etc/profile.d/maven.sh
sudo dos2unix /etc/profile.d/maven.sh
sudo rm -f /usr/local/apache-maven-3.5.4-bin.tar.gz

# ------------------------------------------
# Set up GEMP directory structure
# ------------------------------------------

sudo mkdir -p /env/gemp-swccg/web/
sudo chown -R vagrant:vagrant /env/gemp-swccg
sudo mkdir /logs/
sudo chown vagrant:vagrant /logs

# -----------------------------------------
#   Configure login directory
# -----------------------------------------
sed -i 's@export PATH@export PATH\n\ncd /vagrant@' ~/.bash_profile

# -----------------------------------------
#   Fix windows line endings if necessary at login
# -----------------------------------------
echo 'dos2unix get-card-images.sh' >> ~/.bash_profile
echo 'dos2unix run-gemp.sh' >> ~/.bash_profile
source ~/.bash_profile

# -----------------------------------------
#   Enable system services
# -----------------------------------------
sudo cp /vagrant/vagrant-build/gemp-mysql.cnf /etc/my.cnf.d/
sudo dos2unix /etc/my.cnf.d/gemp-mysql.cnf
sudo systemctl enable --now mariadb

# -----------------------------------------
#   Load seed database
# -----------------------------------------
mysql -u root mysql <<< "CREATE USER 'gemp'@'localhost' IDENTIFIED BY 'gemp';"
mysql -u root mysql <<< "GRANT ALL PRIVILEGES ON *.* TO 'gemp'@'localhost' WITH GRANT OPTION;"
mysql -u root mysql < /vagrant/database_script.sql

# Add test users
mysql -u root gemp-swccg <<< "
INSERT INTO player (name, password, type, last_login_reward, last_ip, create_ip)
VALUES (
	'test1',
	'9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08',
	'aut',
	'20170101',
	'192.168.50.1',
	'192.168.50.1'
),
(
	'test2',
	'9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08',
	'aut',
	'20170101',
	'192.168.50.1',
	'192.168.50.1'
);"

# -----------------------------------------
#   Download card images
# -----------------------------------------
cd /vagrant
./get-card-images.sh

# -----------------------------------------
echo 'Done!'
