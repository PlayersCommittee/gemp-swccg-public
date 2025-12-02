# gemp-swccg
GEMP-SWCCG - server/client for playing Star Wars CCG using a web browser. The program takes care of the rules so you don't have to.

## TOC

* <a href="#quick-start">Quick Start</a>
* <a href="#development-commands">Development Commands</a>
* <a href="#docker">Docker</a>
* <a href="#formats">Formats</a>


<a name="quick-start" />

## Quick Start

### Initial Setup

The fastest way to get GEMP-SWCCG running is using the initialize command:

**macOS/Linux:**
```bash
./bin/gemp initialize
```

This command handles all setup steps including creating required directories, building containers, compiling the application, and starting the server.

**Windows:**

For Windows users, we **strongly recommend** using WSL2 (Windows Subsystem for Linux) to run the setup script and development commands. This provides a native Linux environment and ensures compatibility with our bash-based tooling.

#### Setting up WSL2 on Windows:

1. **Install WSL2:**
   ```powershell
   wsl --install
   ```
   (Requires Windows 10 version 2004+ or Windows 11)

2. **Install a Linux distribution** (Ubuntu recommended):
   ```powershell
   wsl --install -d Ubuntu
   ```

3. **Configure Docker Desktop to use WSL2:**
   - Open Docker Desktop Settings
   - Go to "General" → Enable "Use the WSL2 based engine"
   - Go to "Resources" → "WSL Integration" → Enable integration with your Linux distro
   - Restart Docker Desktop

4. **Clone the repository inside WSL2:**
   ```bash
   # Open WSL2 terminal
   wsl

   # Clone repo in your Linux home directory
   cd ~
   git clone <repository-url>
   cd gemp-swccg
   ./bin/gemp initialize
   ```

**Why WSL2?** The `gemp` development script is a bash script that uses Linux-specific commands and path handling. WSL2 provides a genuine Linux environment, making these scripts work seamlessly. While you can use Git Bash or other emulators, WSL2 provides the best compatibility and performance.

**Alternative:** If you prefer not to use WSL2, you can follow the manual Docker setup steps documented in [src/docker/readme.md](src/docker/readme.md).

### Accessing the Application

After setup completes:
- **URL:** http://localhost:17001/gemp-swccg/
- **Default Login:** asdf / asdf

### Additional Tools / Full Install (Optional)

The [docker readme](src/docker/readme.md) recommends tools like Portainer for container management. However, **the `gemp` script provides all essential functionality** (shell access, logs, status checks) via convenient commands. Portainer is mainly useful if you prefer GUI-based management or need visual resource monitoring (CPU/memory graphs). For typical development, `gemp` is simpler and more efficient. -- If you run into any issues with docker or want to understand the underlying docker structure better, it's documented in the [docker readme](src/docker/readme.md). 

---

<a name="development-commands" />

## Development Commands

The `gemp` script provides convenient commands for common development tasks:

```bash
./bin/gemp [command]
```

### Available Commands

| Command | Description |
|---------|-------------|
| `initialize` | First-time setup (builds containers and compiles application) |
| `start` | Start the application containers |
| `stop` | Stop the application containers |
| `restart` | Restart the application (after code changes) |
| `rebuild` | Recompile the application with Maven |
| `rebuild-fast` | Recompile the application with Maven (skip tests) |
| `reload` | Rebuild and restart in one command |
| `reload-fast` | Rebuild and restart in one command (skip tests) |
| `logs` | Show application logs (tail -f, Ctrl+C to exit) |
| `status` | Check container and application status |
| `shell` | Open a bash shell in the app container |
| `db-shell` | Open MySQL shell for database access |
| `destroy` | Stop and remove containers (preserves database) |
| `reset-db` | Reset the database (WARNING: deletes all data) |
| `help` | Show help message with all commands |

### Common Workflows

**After making code changes (runs all tests):**
```bash
./bin/gemp reload
```

**After making code changes (skip tests for faster iteration):**
```bash
./bin/gemp reload-fast
```

**View logs to debug issues:**
```bash
./bin/gemp logs
```

**Check application status:**
```bash
./bin/gemp status
```

**Access the database:**
```bash
./bin/gemp db-shell
```

---

<a name="docker" />

## Docker

* `src/docker-compose.yml` is used for local development/test environments. `src/docker-compose-prod.yml` contains the minimal changes required to make that same composition work for the production deployment.
* `docker compose build` will build the container images, referring to the configuration file for what settings to use.
* Full installation instructions are in the /docker/readme.md


### Connecting to the local dev/test Gemp server


* After starting the server, point your browser of choice to: http://localhost:17001/gemp-swccg/


<a name="formats" />

## Formats

* The formats are defined in:
  * `gemp-swccg-server/src/main/resources/swccgFormats.json`
  * `gemp-swccg-async/src/main/web/js/gemp-016/cards/CardFilter.js`
  * `gemp-swccg-async/src/main/web/includes/admin/leagueAdmin.html`
* The `CardFilter.js` file controls the display of formats in the deckbuilder and merchant, while `leagueAdmin.html` is for setting up leagues in various formats.  Neither does anything unless the content is updated in `swccgFormats.json`.
* `swccgFormats.json` contains a list of formats in the order they will appear within the Gemp system.
  * The first entry within the list will be the default format used by gemp.
  * The display name _("name" field)_ is used within the database. Once a single game has been played for a format, the name cannot be changed without manually updating the database.
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
* Tenets are sometimes displayed to supplement the list of format settings available in Gemp and provide additional 

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






