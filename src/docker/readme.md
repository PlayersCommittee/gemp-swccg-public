# SWCCG GEMP Docker Setup
Welcome to the wonderful world of containerized installation!

Using Docker, all the fiddly setup and installation details can be coded into scripts so that people like you looking to set up an instance of the software don't have to worry about the database, server, Java installation, and all the hokey prerequisites that go along with it.  Just copy the files, open docker, run the docker-compose, and your instance is ready to be accessed.  


## Container Overview

The entry point is the docker-compose.yml YAML file, which defines two containers and all of their interfaces that are exposed to the outside world and to each other.  This file calls gemp_app.Dockerfile and gemp_db.Dockerfile which are concerned with actually constructing the environments on these two containers. 

gemp_db is straightforward: it's a bare-bones linux instance using the Amazon mirror of the official MariaDB docker image (MariaDB is a variant of MySQL).  It hosts the gemp database and doesn't do anything else.  

gemp_app contains the slightly more involved environment necessary to run the application.  This image starts with the Amazon Corretto image (which manages the configuration for Java) and adds Maven + some other utilities on top.


## Development Tools Needed/Recommended
* [Docker/Docker Desktop](https://www.docker.com/products/docker-desktop/) - required
* A Java IDE; we recommend [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/?section=windows) - recommended
* Java 21 - required (IDEA includes this)
* [Maven 3.9.6](https://archive.apache.org/dist/maven/maven-3/3.9.6/) - required (IDEA includes this)
* A Docker container manager such as [PortainerIO](https://www.portainer.io/) or more specifically this free [Portainer Community Edition](https://docs.portainer.io/start/install-ce) - recommended
* A MySQL manager such as [DBeaver](https://dbeaver.io/) - recommended

## Installation Steps

1. Install [Docker](https://www.docker.com/products/docker-desktop/).
	* Windows Users: make sure that when you install Docker Desktop you select the option to use WSL2 instead of Hyper-V. This option will mimic a Linux environment.  If you are on Windows 10 Home instead, you will not have this option.
	* If you're installing this on Linux, I assume you know more than I do about how to set it up properly.
2. Install your container manager of choice.  I would HIGHLY recommend [PortainerIO](https://www.portainer.io/) or more specifically this [Portainer Community Edition](https://docs.portainer.io/start/install-ce), which itself runs in a docker container and exposes itself as an interactable web page.  This will give you a graphical readout of all your currently running containers, registered images, networks, volumes, and just about anything else you might want, PLUS have interactive command lines for when the GUI just doesn't cut it.  The manager that comes with Docker Desktop by default is pretty much only just barely enough to run portainer with, so don't bother with it otherwise.
3. Pull the git repository down to your host machine; you may have already done this.
4. Open a code editor of your choice and navigate to `{repo-root}/src`.  Open up [docker-compose.yml](docker-compose.yml) and change the defaults to suit your needs:
	1. Note all the relative paths under each volume/source: these are all paths on your host system.  If you want e.g. the database to be in a different location than what's listed, alter these relative paths to something else on your host system.
	2. In the Docker [.env](./.env) file note all of the username/password fields.  If you are hosting this for something other than personal development, be sure to change all of these to something else.
	3. Note the two "published" ports: 17001 for the app, and 35001 for the db.  These are the ports that you will be accessing the site with (and the db if you connect with a database manager). If you are hosting this for something other than personal development, consider changing these to something else.  **DO NOT** change the "target" ports, these targets are the ports that are used internally by Docker networking.
5. In `{repo-root}/src/docker-compose.yml`, comment out lines 42-48 (the Command instruction) by placing a `#` in front of each line.  This section runs Gemp, but it expects the jar file to be in place, which is not the case until you have built it.
6. Open a command line and navigate to `src`. 
	* Run the command `docker compose build`
	* Run the command `docker compose up -d`
	* You should see `Starting gemp_app....done` and `Starting gemp_db....done` at the end.  
	* This process will take a while the first time you do it, and will be near instantaneous every time after.
7. The database should have automatically created the gemp databases that are needed.  
	* You can verify this by connecting to the database on your host machine with your DB manager of choice (I recommend [DBeaver](https://dbeaver.io/)).  
	* It is exposed on localhost:35001 (unless you changed this port in step 4.2) and uses the user/pass of `gempuser`/`gemppassword` (unless you changed this in step 4.2).  
	* If you can see the `gemp_db` database with `collection` and other tables, you're golden.  
8. Open a terminal in the Docker container
	* Using Portainer or Docker Desktop open a terminal in the `gemp_swccg_app_1` container
		* if using portainer.io, 
			* log in
			* select your 'Local' endpoint
			* click the Containers tab on the left
			* click the `>_` icon next to gemp_app and click the Connect button
		* If using Docker Desktop
			* Open Docker Desktop
			* Select the "Container" option in the left navbar
			* expand the `gemp_swccg_app_1` container
			* click the actions button and select `Open in Terminal`
9. You should already be in the main Gemp folder at `opt/gemp-swccg`.
	* Use Maven to compile the application:	`mvn install`
	* This process will take upwards of 5-10 minutes.  
	* You should see a green "BUILD SUCCESS" when it is successfully done.  In portainer.io or another rich command line context, you should see lots of red text if it failed.
10. On your host machine, open up `src/docker-compose.yml` again and uncomment lines 42-48, so that the Command instruction is restored.
11. On your host machine, rebuild your docker container
	* In a terminal navigate to `src/docker` and run `docker compose down` and then `docker compose build` and then `docker compose up -d`
	* NOTE: You will not normally need to rebuild the docker container. Normally, after compiling code updates with `mvn install` you will only need to restart the container, not rebuild it. You can do that through the Docker or Portainer interface (look for a restart button on the container) or you can run `docker compose restart`
12. If all has gone as planned, you should now be able to navigate to your own personal instance of Gemp.  
	* Open your browser of choice and navigate to http://localhost:17001/gemp-swccg/ .  (If you need a different port to be bound to, then repeat step 4 and edit the exposed port, then repeat step 9 to load those changes.)
13. If you're presented with the home page, log in using the default `asdf`/`asdf` user. It's possible for the login page to present but login itself to fail if configured incorrectly, so don't celebrate until you see the (empty) lobby.  If you get that far, then congrats, you now have a working local version of Gemp.

At this point, editing the code is a matter of changing the files on your local machine, re-compiling the code base in IDEA (or via `mvn install`), and then restarting the container.  




