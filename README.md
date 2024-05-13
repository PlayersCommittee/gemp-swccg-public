# gemp-swccg
GEMP-SWCCG - server/client for playing Star Wars CCG using a web browser. The program takes care of the rules so you don't have to.

## TOC

* <a href="#docker">Docker</a>
* <a href="#vagrant">Vagrant</a>
  * <a href="README_vagrant_windows.md"><em>Vagrant for Windows</em></a>
* <a href="#formats">Formats</a>


<a name="docker" />

## Docker

* `docker/docker-compose.yml` is used both for the production environment and local development/test environments.
* `docker compose build` will build the container images, referring to the configuration file for what settings to use.
* Full installation instructions are in the /docker/readme.md


### Connecting to the local dev/test Gemp server


* After starting the server, point your browser of choice to: http://localhost:17001/gemp-swccg/


<a name="formats" />

## Formats

* The formats are defined in:
  * `gemp-swccg-server/src/main/resources/swccgFormats.json`
  * `gemp-swccg-async/src/main/web/js/gemp-016/cardFilter.js`
  * `gemp-swccg-async/src/main/web/includes/admin/leagueAdmin.html`
* The `cardFilter.js` file controls the display of formats in the deckbuilder and merchant, while `leagueAdmin.html` is for setting up leagues in various formats.  Neither does anything unless the content is updated in `swccgFormats.json`.
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






