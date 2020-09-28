package com.gempukku.swccgo.common;

/**
 * This is used for misc keywords on a card that do not fit into another specific enum,
 * but still need to be filtered on.
 */
public enum Keyword implements Filterable {
    ACCOUNTANT("Accountant", true),
    ADMIRAL("Admiral", true),
    ASSASSIN("Assassin", true),
    ASTEROID("Asteroid", true),
    BANTHA("Bantha", true),
    BATTLESHIP("Battleship", true),
    BIKER_SCOUT("Biker Scout", true),
    BOUNTY("Bounty", false),
    BOUNTY_HUNTER("Bounty Hunter", true),
    BOWCASTER("Bowcaster", true),
    BLACK_SQUADRON("Black Squadron", true),
    BLACK_SUN_AGENT("Black Sun Agent", true),
    BLASTER("Blaster", true),
    BLASTER_RIFLE("Blaster Rifle", true),
    BLUE_SQUADRON("Blue Squadron", true),
    BRAVO_SQUADRON("Bravo Squadron", true),
    CADET("Cadet", true),
    CAN_RELEASE_CAPTIVES("Can release captives", false),
    CANNON("Cannon", false),
    CANYON("Canyon", true),
    CAPTAIN("Captain", true),
    CLONE_TROOPER("Clone Trooper", true),
    CLOUD_CITY_LOCATION("Cloud City Location", false),
    CLOUD_CITY_TROOPER("Cloud City Trooper", true),
    CLOUD_SECTOR("Cloud Sector", false),
    COMMANDER("Commander", true),
    CORUSCANT_GUARD("Coruscant Guard", true),
    CRIMSON_DAWN("Crimson Dawn", true),
    CRUISER("Cruiser", true),
    DEATH_STAR_TROOPER("Death Star Trooper", true),
    DEATH_TROOPER("Death Trooper", true),
    DEJARIK("Dejarik", true),
    DESERT("Desert", true),
    DEVICE_THAT_DEPLOYS_ON_DROIDS("Device that deploys on droids", false),
    DEWBACK("Dewback", true),
    DFS_SQUADRON("DFS Squadron", true),
    DH17_BLASTER("DH-17 Blaster", true),
    DISARMING_CARD("Disarming card", false),
    DOCKING_BAY("Docking Bay", true),
    DROID_CONTROL_SHIP("Droid Control Ship", true),
    ECHO_BASE_TROOPER("Echo Base Trooper", true),
    ELECTROPOLE("Electropole", true),
    ENCLOSED("Enclosed", true),
    EOPIE("Eopie", true),
    EWOK_DEVICE("Ewok Device", false),
    EWOK_VEHICLE("Ewok Vehicle", false),
    EWOK_WEAPON("Ewok Weapon", false),
    FAMBAA("Fambaa", true),
    FARM("Farm", true),
    FEMALE("Female", true),
    FORCE_DRAIN_MULTI_PARTICIPANT("May participate in multiple Force drains", false),
    FOREST("Forest", true),
    FORCE_PIKE("Force Pike", true),
    FUSION_GENERATOR("Fusion Generator", true),
    GAMBLER("Gambler", true),
    GANGSTER("Gangster", true),
    GAS_MINER("Gas Miner", true),
    GENERAL("General", true),
    GOLD_SQUADRON("Gold Squadron", true),
    GRAY_SQUADRON("Gray Squadron", true),
    GREEN_SQUADRON("Green Squadron", true),
    GUNNER("Gunner", true),
    HANDMAIDEN("Handmaiden", true),
    HOLOGRAM("Hologram", true),
    HOLOSITE("Holosite", true),
    IMPERIAL_TROOPER_GUARD("Imperial Trooper Guard", true),
    INFANTRY_BATTLE_DROID("Infantry Battle Droid", true),
    INFORMATION_BROKER("Information Broker", true),
    INQUISITOR("Inquisitor", true),
    ION_CANNON("Ion Cannon", false),
    ISB_AGENT("ISB Agent", true),
    JABBAS_PALACE_SITE("Jabba's Palace Site", false),
    JAWA_WEAPON("Jawa Weapon", false),
    JEDI_COUNCIL_MEMBER("Jedi Council Member", true),
    JEDI_TEST_1("Jedi Test #1", true),
    JEDI_TEST_2("Jedi Test #2", true),
    JEDI_TEST_3("Jedi Test #3", true),
    JEDI_TEST_4("Jedi Test #4", true),
    JEDI_TEST_5("Jedi Test #5", true),
    JEDI_TEST_6("Jedi Test #6", true),
    JUNGLE("Jungle", true),
    KAADU("Kaadu", true),
    SCAVENGER("Scavenger", true),
    LANDSPEEDER("Landspeeder", false),
    LASER_CANNON("Laser Cannon", false),
    LEAD_STARFIGHTER("Lead Starfighter", true),
    LEADER("Leader", true),
    LIGHTSABER("Lightsaber", true),
    MALE("Male", true),
    MARKER_1("1st Marker", false),
    MARKER_2("2nd Marker", false),
    MARKER_3("3rd Marker", false),
    MARKER_4("4th Marker", false),
    MARKER_5("5th Marker", false),
    MARKER_6("6th Marker", false),
    MARKER_7("7th Marker", false),
    MAY_NOT_BE_FORFEITED_IN_BATTLE("May not be forfeited in battle", false),
    MAY_MOVE("May move", true),
    //MAZS_PALACE_LOCATION("Maz's Palace Location", false),
    MAZS_CASTLE_LOCATION("Maz's Castle Location", false),
    MEDIUM_TRANSPORT("Medium Transport", false),
    MINE("Mine", true),
    MISSILE("Missile", true),
    MOFF("Moff", true),
    MON_CALAMARI("Mon Calamari", true),
    MUSICIAN("Musician", true),
    MUST_BE_FORFEITED_IN_BATTLE("Must be forfeited in battle", false),
    MUST_BE_FORFEITED_BEFORE_OTHER_CHARACTERS("Must be forfeited before other characters", false),
    NIGHTTIME_CONDITIONS("Nighttime Conditions", true),
    NO_HYPERDRIVE("No Hyperdrive", false),
    OBSIDIAN_SQUADRON("Obsidian Squadron", true),
    OFFICER_BATTLE_DROID("Officer Battle Droid", true),
    ONYX_SQUADRON("Onyx Squadron", true),
    OPERATIVE("Operative", true),
    PADAWAN("Padawan", true),
    PARASITE("Parasite", true),
    PHOENIX_SQUADRON("Phoenix Squadron", true),
    PIRATE("Pirate", true),
    PIT("Pit", false),
    PRISON("Prison", true),
    PROTON_TORPEDOES("Proton Torpedoes", false),
    RED_SQUADRON("Red Squadron", true),
    RESISTANCE_AGENT("Resistance Agent", true),
    RECRUIT("Recruit", true),
    REFINERY("Refinery", true),
    RIFLE("Rifle", true),
    ROGUE_SQUADRON("Rogue Squadron", true),
    RONTO("Ronto", true),
    ROYAL_GUARD("Royal Guard", true),
    ROYAL_NABOO_SECURITY("Royal Naboo Security Officer", true),
    SABER_SQUADRON("Saber Squadron", true),
    SANDCRAWLER("Sandcrawler", true),
    SANDCRAWLER_SITE("Sandcrawler site", false),
    SANDSPEEDER("Sandspeeder", false),
    SANDTROOPER("Sandtrooper", true),
    SCYTHE_SQUADRON("Scythe Squadron", true),
    SCIMITAR_SQUADRON("Scimitar Squadron", true),
    SCOUT("Scout", true),
    SECURITY_BATTLE_DROID("Security Battle Droid", true),
    SEEKER("Seeker", true),
    SENATOR("Senator", true),
    SHIP_DOCKING_CAPABILITY("Ship-docking Capability", false),
    SKIFF("Skiff", false),
    SMUGGLER("Smuggler", true),
    SNOWSPEEDER("Snowspeeder", false),
    SNOWTROOPER("Snowtrooper", true),
    SPACEPORT_SITE("Spaceport Site", false),
    SPEEDER("Speeder",false),
    SPY("Spy", true),
    STARSHIP_WEAPON_THAT_DEPLOYS_ON_CAPITALS("Starship weapon that deploys on capitals", false),
    STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS("Starship weapon that deploys on starfighters", false),
    STORMTROOPER("Stormtrooper", true),
    SWAMP("Swamp", true),
    SWOOP("Swoop", true),
    TAUNTAUN("Tauntaun", true),
    TAX_COLLECTOR("Tax Collector", true),
    THEED_PALACE_SITE("Theed Palace Site", false),
    THIEF("Thief", true),
    TRACTOR_BEAM("Tractor Beam", true),
    TRANSPORT_SHIP("Transport ship", true),
    TROOPER("Trooper", true),
    TURBOLASER_BATTERY("Turbolaser Battery", false),
    UTINNI_EFFECT_THAT_RETRIEVES_FORCE("Utinni Effect that retrieves Force", false),
    UWING("U-Wing", true),
    VIBRO_AX("Vibro-Ax", true),
    WAMPA("Wampa", true),
    WAR_ROOM("War Room", true),
    XIZORS_PALACE_SITE("Xizor's Palace Site", false);


    private String _humanReadable;
    private boolean _infoDisplayable;

    Keyword(String humanReadable) {
        this(humanReadable, false);
    }

    Keyword(String humanReadable, boolean infoDisplayable) {
        _humanReadable = humanReadable;
        _infoDisplayable = infoDisplayable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public boolean isInfoDisplayable() {
        return _infoDisplayable;
    }
}
