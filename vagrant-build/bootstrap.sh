#!/usr/bin/env bash

# -----------------------------------------
#   Install dependencies
# -----------------------------------------

sudo yum -y install unzip vim
sudo yum -y install mysql mariadb-server
sudo yum -y install java-1.8.0-openjdk-devel

# Install maven
cd /usr/local
sudo wget http://www-eu.apache.org/dist/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.tar.gz
sudo tar xzf apache-maven-3.5.3-bin.tar.gz
sudo ln -s apache-maven-3.5.3 maven
sudo cp /vagrant/vagrant-build/maven.sh /etc/profile.d/maven.sh
sudo rm -f /usr/local/apache-maven-3.5.3-bin.tar.gz

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
#   Enable system services
# -----------------------------------------
sudo cp /vagrant/vagrant-build/gemp-mysql.cnf /etc/my.cnf.d/
sudo systemctl enable --now mariadb

# -----------------------------------------
#   Load seed database
# -----------------------------------------
mysql -u root mysql <<< "CREATE USER 'gemp-lotr'@'localhost' IDENTIFIED BY 'gemp-lotr';"
mysql -u root mysql <<< "GRANT ALL PRIVILEGES ON *.* TO 'gemp-lotr'@'localhost' WITH GRANT OPTION;"
mysql -u root mysql < /vagrant/database_script.sql

# -----------------------------------------
echo 'Done!'
