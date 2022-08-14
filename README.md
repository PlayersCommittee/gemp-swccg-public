# gemp-swccg
GEMP-SWCCG - server/client for playing Star Wars CCG using a web browser. The program takes care of the rules so you don't have to.

## TOC

* <a href="#docker">Docker</a>
* <a href="#vagrant">Vagrant</a>
* <a href="#formats">Formats</a>


<a name="docker" />

## Docker


### Dockerfiles

* `Dockerfile` is the container configuration for gemp.
  - The environment variables that configure gemp are set to some default values in the Dockerfile.
  - The container is based on Ubuntu Focal for compatibility with development environments.
  - The container uses OpenJDK 11. Newer versions of OpenJDK, such as OpenJDK 18, have compatibility issues with gemp.
  - Both `test-log4j.xml` and `prod-log4j.xml` are copied in to the container so it can be used in either a test or production context.
  - Both test and prod log4j files configure logs to go to `/logs`.
  - gemp replays are written to `/opt/gemp-swccg/replay`. The container filesystem is ephemeral and will not persist the contents across runs.
* `db.Dockerfile` is the container configuration for MariaDB when used as a backend database in the development/test context.
  - The Dockerfile copies `./database_script.sql` and `./initial_user_setup.sql` in to the container to populate the database in a development/test context.
  - Do NOT use this container to operate a production workload.
  - The database name must be `gemp-swccg` to maintain compatibility with `database_script.sql`.


### Docker-Compose

* `docker-compose.yml` can be used, _instead of the utility scripts below,_ to create a development/test environment.
* Docker compose will build the container images.
* Before bringing up the test environment using `docker-compose`, compile gemp.

```bash
mvn clean install
docker-compose up
```


### Utility Scripts

* `docker_build.sh` will compile gemp and create the relevant container image(s).
* `docker_run_db.sh` will run the backend database in a development, or test, context.
* `docker_run_app.sh` will run the gemp server in a development, or test, context.
* `docker_purge.sh` will delete old stopped containers and images.


### Connecting to the local dev/test gemp server


* After starting the server, point your browser of choice to: http://0.0.0.0:8080/gemp-swccg/



<a name="vagrant" />

## Running a Dev VM with Vagrant

Vagrant is a tool for automating the creation of virtual machines. This allows us to mimic the server environment more closely and have a single set of commands to run the program, regardless of what OS is used for development. Your dev machine can be used to code and compile in whatever IDE you like. The code is automatically shared with the VM, which is used to run it. The virtual machine is also capable of compiling the source using maven.

### Setup

Install vagrant (https://www.vagrantup.com/downloads.html) and virtualbox (https://www.virtualbox.org/wiki/Downloads) for your OS.

From the command line run:
```bash
vagrant plugin install vagrant-vbguest
```

This plugin keeps the host and guest OS in sync and allows directories to be seamlessly shared.


### Spinning up the VM

The following command will spin up the virtual machine, install all dependencies needed for running and compiling GEMP, and create the database if it does not already exist. From the `gemp-swccg` repository root directory, run the following command:
```bash
vagrant up
```

### Logging into the VM

Log into the VM by using the following command from the repository root:
```bash
vagrant ssh
```

### Compiling Gemp

The VM can use pre-compiled .jar and .zip files if they are located in the `gemp-swccg-async/target/` directory. However, you can also compile on the VM directly.

After running `vagrant ssh`, run the following command ON THE VM:
```
mvn clean install
```

### Running GEMP

From the SSH login direcory on the VM (`/vagrant`) run:
```bash
./run-gemp.sh
```

You can now access GEMP by visiting `http://192.168.60.94:8080/gemp-swccg/` in your browser.

The bootstrap script automatically creates 2 test admin accounts, `test1` and `test2`, with the password "test". It also creates the `Librarian` user with the password "test" and loads several sample decks. When the server first starts, it is in non-operational standby mode which does not allow games to be started. To enable operational mode, when logged in visit `http://192.168.60.94:8080/gemp-swccg/admin.html` in your browser and click the "Startup" link.

### Other VM Commands

* `vagrant halt`: Hard shutdown of the VM
* `vagrant suspend`: Soft shutdown of the VM (this is faster)
* `vagrant destroy`: Remove VM from your system



<a name="formats" />

## Formats

* The formats are defined in:
  * `gemp-swccg-server/src/main/resources/swccgFormats.json`
  * `gemp-swccg-async/src/main/web/js/gemp-016/cardFilter.js`
  * `gemp-swccg-async/src/main/web/leagueAdmin.html`
* The `cardFilder.js` and `leagueAdmin.html` files are just admin pages, so the lists there are used for interacting with the content in `swccgFormats.json`.
* `swccgFormats.json` contains a list of formats in the order they will appear within the gemp system.
  * The first entry within the list will be the default format used by gemp.
  * The The display name _("name" field)_ is used within the database. Once a single game has been played for a format, the name can not be changed without manually updating the database.
  * The format of the json records is not unmarshaled is a static typed way, so you can optionally add _"comment"_ fields.

### Disabling Formats

* Formats, once used, should never be deleted from `swccgFormats.json`.
* **Disable** a format by setting the `"hall":false` field:

```javascript
  {
    "name":"Dream Cards",
    "code":"dream_cards",
    "set":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14],
    "hall":false
  },

```

### Links to Tenets

* Tenets are the guidelines behind the format.
* The tenet guides any person to be able to update the format with consistency.
* Tenets are sometimes displayed to suplement the list of format settings available in Gemp and provide additional 

```javascript
  {
    "comment": "use utinni_maker.py to update the Utinni format. Do not update manually.",
    "name":"Utinni! (Jawas Only)",
    "code":"utinni",
    "setComment": "All sets are always valid",
    "set":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14],
    "bannedComment": "generate the list of banned cards with utinni_maker.py",
    "bannedIconsComment": "get reference list of icons from gemp-swccg-common/src/main/java/com/gempukku/swccgo/common/Icon.java",
    "bannedIcons":[
      "DARK_JEDI_MASTER",
      "FIRST_ORDER",
      "JEDI_MASTER",
      "SITH",
      "PERMANENT_WEAPON",
      "SIDIOUS",
      "SKYWALKER"
    ],
    "tenetsLink": "https://www.starwarsccg.org/utinni/"
  }

```

### Customizing the game time

* The `defaultGameTimerMinutes` field can be used to customize the time per game on a format.

```javascript
  {
    "name":"Open (100 cards)",
    "code":"open_100_card",
    "set":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14],
    "deckSize":100,
    "defaultGameTimerMinutes":90,
    "tenetsLink": "https://www.starwarsccg.org/100card/"
  },
```


### Banning by Icon

* Get the list of icons from `gemp-swccg-common/src/main/java/com/gempukku/swccgo/common/Icon.java`
* Use the `bannedIcons` item:

```javascript
    "bannedIcons":[
      "DARK_JEDI_MASTER",
      "FIRST_ORDER",
      "JEDI_MASTER",
      "SITH",
      "PERMANENT_WEAPON",
      "SIDIOUS",
      "SKYWALKER"
    ],
```






