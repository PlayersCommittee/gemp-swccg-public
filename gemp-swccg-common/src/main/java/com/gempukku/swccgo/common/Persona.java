package com.gempukku.swccgo.common;

/**
 * Represents each persona in the SWCCG card game.
 */
public enum Persona implements Filterable {
    // Characters
    _4_LOM("4-LOM"),
    AHSOKA("Ahsoka"),
    AMIDALA("Amidala"),
    ANAKIN("Anakin"),
    AURRA("Aurra"),
    BB8("BB-8"),
    BECKETT("Beckett"),
    BEAUMONT("Beaumont"),
    BLIZZARD_1("Blizzard 1"),
    BOBA_FETT("Boba Fett"),
    BOOSTER("Booster"),
    BOSSK("Bossk"),
    C3PO("C-3PO"),
    CAD("Cad"),
    CAL("Cal"),
    CHEWIE("Chewie"),
    CONNIX("Connix"),
    CORRAN_HORN("Corran Horn"),
    CRACKEN("Cracken"),
    DASH("Dash"),
    DENGAR("Dengar"),
    DIN("Din"),
    DJ("DJ"),
    DOALLYN("Doallyn"),
    DOFINE("Dofine"),
    DOOKU("Dooku"),
    DS_61_2("DS-61-2"),
    DS_61_3("DS-61-3"),
    DUTCH("Dutch"),
    ELIS("Elis"),
    EMPEROR("Emperor"),
    EZRA("Ezra"),
    GIDEON("Gideon"),
    GREEN_LEADER("Green Leader"),
    GRIEVOUS("Grievous"),
    GRONDORN("Grondorn"),
    GUNRAY("Gunray"),
    HAAKO("Haako"),
    HAN("Han"),
    HET("Het"),
    IG88("IG-88"),
    JABBA("Jabba"),
    JAINA("Jaina"),
    JANGO_FETT("Jango"),
    JAR_JAR("Jar Jar"),
    JENDON("Jendon"),
    JERUS("Jerus"),
    JONUS("Jonus"),
    JUNKIN("Junkin"),
    KRENNIC("Krennic"),
    KYLO("Kylo"),
    L3_37("L3-37"),
    LADY_VADER("Lady Vader"),
    LANDO("Lando"),
    LAUDICA("Laudica"),
    LEIA("Leia"),
    LOBOT("Lobot"),
    LUKE("Luke"),
    MARA_JADE("Mara Jade"),
    MARA_SKYWALKER("Mara Skywalker"),
    MARGO("Margo"),
    MARQUAND("Marquand"),
    MACE("Mace"),
    MAUL("Maul"),
    MAZ("Maz"),
    MCQUARRIE("McQuarrie"),
    MELSHI("Melshi"),
    MON_MOTHMA("Mon Mothma"),
    MOTTI("Motti"),
    NARTHAX("Narthax"),
    OBIWAN("Obi-Wan"),
    OCHI("Ochi"),
    ODD_BALL("Odd ball"),
    OS_72_1("OS-72-1"),
    OS_72_2("OS-72-2"),
    OZZEL("Ozzel"),
    PALPATINE("Palpatine"),
    PANAKA("Panaka"),
    PAPLOO("Paploo"),
    PEROSEI("Perosei"),
    PIETT("Piett"),
    PLO("Plo Koon"),
    PRE_VIZSLA("Pre Vizsla"),
    PROXIMA("Proxima"),
    PRYDE("Pryde"),
    PUCK("Puck"),
    QUEENS_ROYAL_STARSHIP("Queen's Royal Starship"),
    QUIGON("Qui-Gon"),
    QIRA("Qi'ra"),
    R2D2("R2-D2"),
    RAYC("Rayc"),
    RIC("Ric"),
    RED_LEADER("Red Leader"),
    REY("Rey"),
    ROSE("Rose"),
    SABE("Sabe"),
    SACHE("Sache"),
    SEFLA("Sefla"),
    SEVENTH_SISTER("Seventh Sister"),
    SHAWN("Shawn"),
    SIDIOUS("Sidious"),
    SNAP("Snap Wexley"),
    SNOKE("Snoke"),
    SON_OF_VADER("Son Of Vader"),
    TALLIE_LINTRA("Tallie"),
    TARKIN("Tarkin"),
    THEDIT("Thedit"),
    THRAWN("Thrawn"),
    TIGRAN("Tigran"),
    TYCHO("Tycho"),
    VADER("Vader"),
    VAL("VAL"),
    VEERS("Veers"),
    VOS("Vos"),
    VUL("Vul"),
    WEDGE("Wedge"),
    WUHER("Wuher"),
    YODA("Yoda"),
    ZUCKUSS("Zuckuss"),

    // Starships
    BESTOON_LEGACY("Bestoon Legacy"),
    BLACK_1("Black 1"),
    BLACK_2("Black 2"),
    BLACK_3("Black 3"),
    BLOCKADE_FLAGSHIP("Blockade Flagship"),
    BLUE_11("Blue 11"),
    BLUE_SQUADRON_1("Blue Squadron 1"),
    EXECUTOR("Executor"),
    FALCON("Falcon"),
    FIRST_LIGHT("First Light"),
    GOLD_1("Gold 1"),
    GREEN_SQUADRON_1("Green Squadron 1"),
    GREEN_SQUADRON_3("Green Squadron 3"),
    HOME_ONE("Home One"),
    HOUNDS_TOOTH("Hound's Tooth"),
    IG2000("IG-2000"),
    INVISIBLE_HAND("Invisible Hand"),
    LIBERTINE("Libertine"),
    MIST_HUNTER("Mist Hunter"),
    ONYX_1("Onyx 1"),
    PULSAR_SKATE("Pulsar Skate"),
    PUNISHING_ONE("Punishing One"),
    RADIANT_VII("Radiant VII"),
    RED_1("Red 1"),
    RED_2("Red 2"),
    RED_5("Red 5"),
    RIO("Rio"),
    SCIMITAR_2("Scimitar 2"),
    SLAVE_I("Slave I"),
    STEADFAST("Steadfast"),
    TANTIVE_IV("Tantive IV"),
    VADERS_CUSTOM_TIE("Vader's Custom TIE"),

    // Vehicles
    JABBAS_SAIL_BARGE("Jabba's Sail Barge"),

    // Weapons
    AHSOKAS_LIGHTSABERS("Ahsoka's Lightsabers"),
    AHSOKAS_SHOTO_LIGHTSABER("Ahsoka's Shoto Lightsaber"),
    ANAKINS_LIGHTSABER("Anakin's Lightsaber"),
    AURRAS_BLASTER_RIFLE("Aurra Sing's Blaster Rifle"),
    KYLOS_LIGHTSABER("Kylo's Lightsaber"),
    LADY_VADERS_BLASTER_RIFLE("Lady Vader's Blaster Rifle"),
    LEIAS_BLASTER_RIFLE("Leia's Blaster Rifle"),
    LEIAS_LIGHTSABER("Leia's Lightsaber"),
    LUKES_LIGHTSABER("Luke's Lightsaber"),
    MARA_JADES_LIGHTSABER("Mara Jade's Lightsaber"),
    MARA_SKYWALKERS_LIGHTSABER("Mara Skywalker's Lightsaber"),
    MAULS_DOUBLE_BLADED_LIGHTSABER("Maul's Double-Bladed Lightsaber"),
    QUIGON_JINNS_LIGHTSABER("Qui-Gon Jinn's Lightsaber"),
    SON_OF_VADERS_LIGHTSABER("Son Of Vader's Lightsaber"),
    VADERS_LIGHTSABER("Vader's Lightsaber");



    private String _humanReadable;

    Persona(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public Persona getCrossedOverPersona() {
        if (equals(ANAKIN)) return VADER;
        if (equals(LADY_VADER)) return LEIA;
        if (equals(LEIA)) return LADY_VADER;
        if (equals(LUKE)) return SON_OF_VADER;
        if (equals(MARA_JADE)) return MARA_SKYWALKER;
        if (equals(MARA_SKYWALKER)) return MARA_JADE;
        if (equals(SON_OF_VADER)) return LUKE;
        if (equals(VADER)) return ANAKIN;
        if (equals(ANAKINS_LIGHTSABER)) return VADERS_LIGHTSABER;
        if (equals(LADY_VADERS_BLASTER_RIFLE)) return LEIAS_BLASTER_RIFLE;
        if (equals(LEIAS_BLASTER_RIFLE)) return LADY_VADERS_BLASTER_RIFLE;
        if (equals(LUKES_LIGHTSABER)) return SON_OF_VADERS_LIGHTSABER;
        if (equals(MARA_JADES_LIGHTSABER)) return MARA_SKYWALKERS_LIGHTSABER;
        if (equals(MARA_SKYWALKERS_LIGHTSABER)) return MARA_JADES_LIGHTSABER;
        if (equals(SON_OF_VADERS_LIGHTSABER)) return LUKES_LIGHTSABER;
        if (equals(VADERS_LIGHTSABER)) return ANAKINS_LIGHTSABER;
        return this;
    }
}
