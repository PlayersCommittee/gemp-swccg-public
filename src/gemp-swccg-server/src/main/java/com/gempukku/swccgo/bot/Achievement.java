package com.gempukku.swccgo.bot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum representing all bot achievements.
 *
 * Each achievement has:
 * - A unique bit position (0-143) for storage in a bitfield
 * - A key (for persistence compatibility with Python bot)
 * - A quote (displayed when unlocked)
 * - A trigger type (how it's unlocked)
 * - Optional card match criteria
 *
 * Achievement bitfield uses 18 bytes (144 bits) stored as BINARY(18) in MySQL.
 *
 * Trigger types:
 * - CARD_IN_PLAY: Single card appears on board
 * - MY_CARD: Bot's card appears
 * - THEIR_CARD: Opponent's card appears
 * - CARDS_TOGETHER: Multiple cards at same location
 * - CARDS_AT_SITE: Cards at specific site type
 * - CARD_KILLED: Card was removed from board
 * - CARD_KILLED_BY: Card killed while another card present
 * - DAMAGE: Damage threshold met
 * - ROUTE_SCORE: Route score threshold
 * - FIRST_ROUTE_SCORE: First time reaching score threshold
 * - COMEBACK: Special comeback condition
 * - SPEEDRUN: Win in few turns
 * - PACIFIST: Win without battles
 * - BATTLES_WON: Win multiple battles
 * - LOCATIONS_CONTROLLED: Control multiple locations
 * - FORCE_REMAINING: End with high force
 * - HAND_SIZE: Large hand size
 * - GAMES_PLAYED: Play many games
 * - ACHIEVEMENTS: Unlock many achievements
 * - AST_SCORE: High total astrogation score
 * - LUCKY_WIN: Win from behind
 * - UNLUCKY_LOSS: Lose from ahead
 * - HOLIDAY_GAME_COMPLETE: Complete game during holiday
 * - HOLIDAY_CARD_IN_PLAY: Card in play during holiday
 * - HOLIDAY_SELLABLE_ROUTE: Good route score during holiday
 * - HOLIDAY_GAMES_PLAYED: Multiple games during holiday
 */
public enum Achievement {
    // =========================================================================
    // Single Card Appearances (0-52) - 53 achievements
    // =========================================================================
    BOSSK(0, "achievement_bossk", "Thinking takes too long. Action gets things done.",
          TriggerType.CARD_IN_PLAY, "bossk", CardType.CHARACTER),
    FORTUNA(1, "achievement_fortuna", "I take you to Jabba now.",
            TriggerType.CARD_IN_PLAY, "fortuna", CardType.CHARACTER),
    SHMI(2, "achievement_shmi", "You can't stop the change, any more than you can stop the suns from setting.",
         TriggerType.CARD_IN_PLAY, "shmi", CardType.CHARACTER),
    RETURNED(3, "achievement_returned", "I Have Died Before.",
             TriggerType.CARD_IN_PLAY, "emperor returned", CardType.CHARACTER),
    JYN(4, "achievement_jyn", "If we can make it to the ground, we'll take the next chance. And the next. On and on until we win... or the chances are spent.",
        TriggerType.CARD_IN_PLAY, "jyn", CardType.CHARACTER),
    POE(5, "achievement_poe", "Permission to hop in an X-Wing and blow something up?",
        TriggerType.CARD_IN_PLAY, "poe", CardType.CHARACTER),
    CHIRRUT(6, "achievement_chirrut", "I'm one with the Force, and the Force is with me.",
            TriggerType.CARD_IN_PLAY, "chirrut", CardType.CHARACTER),
    JERJERROD(7, "achievement_jerjerrod", "I assure you, Lord Vader. My men are working as fast as they can.",
              TriggerType.CARD_IN_PLAY, "jerjerrod", CardType.CHARACTER),
    KYLO(8, "achievement_kylo", "Forgive me. I feel it again... the call from light.",
         TriggerType.CARD_IN_PLAY, "kylo ren", CardType.CHARACTER),
    SABER(9, "achievement_saber", "An elegant weapon, from a more civilized age.",
          TriggerType.CARD_IN_PLAY, "lightsaber", CardType.WEAPON),
    DOOKU(10, "achievement_dooku", "Twice the pride, double the fall.",
          TriggerType.CARD_IN_PLAY, "dooku", null),
    SENATE(11, "achievement_senate", "I am the Senate!",
           TriggerType.CARD_IN_PLAY, "senator palpatine", null),
    SIDIOUS(12, "achievement_sidious", "Power! Unlimited Power!",
            TriggerType.CARD_IN_PLAY, "sidious", null),
    KENOBI(13, "achievement_kenobi", "Hello there.",
           TriggerType.CARD_IN_PLAY, "general kenobi", null),
    REY(14, "achievement_rey", "The garbage'll do!",
        TriggerType.CARD_IN_PLAY, "rey", CardType.CHARACTER),
    ACKBAR(15, "achievement_ackbar", "It's a trap!",
           TriggerType.CARD_IN_PLAY, "ackbar", null),
    AHSOKA(16, "achievement_ahsoka", "I am no Jedi.",
           TriggerType.CARD_IN_PLAY, "ahsoka", CardType.CHARACTER),
    JANGO(17, "achievement_jango", "I'm just a simple man trying to make my way in the universe.",
          TriggerType.CARD_IN_PLAY, "jango", CardType.CHARACTER),
    WATTO(18, "achievement_watto", "Mind tricks don't work on me.",
          TriggerType.CARD_IN_PLAY, "watto", CardType.CHARACTER),
    QUIGON(19, "achievement_quigon", "There's always a bigger fish.",
           TriggerType.CARD_IN_PLAY, "qui-gon", CardType.CHARACTER),
    GUNRAY(20, "achievement_gunray", "In time, the suffering of your people will persuade you to see our point of view.",
           TriggerType.CARD_IN_PLAY, "gunray", CardType.CHARACTER),
    KAMINO(21, "achievement_kamino", "It Ought To Be Here... But It Isn't...",
           TriggerType.CARD_IN_PLAY, "kamino", CardType.LOCATION),
    YODA(22, "achievement_yoda", "Do. Or do not. There is no try.",
         TriggerType.CARD_IN_PLAY, "yoda", CardType.CHARACTER),
    LANDO(23, "achievement_lando", "Everything you've heard about me is true.",
          TriggerType.CARD_IN_PLAY, "lando", CardType.CHARACTER),
    GONK(24, "achievement_gonk", "Gonk, Gonk.",
         TriggerType.CARD_IN_PLAY, "eegee", CardType.CHARACTER),
    K2SO(25, "achievement_k2so", "Jyn, I'll be there for you. Cassian said I had to.",
         TriggerType.CARD_IN_PLAY, "2so", null),
    THRAWN(26, "achievement_thrawn", "To defeat an enemy, you must know them. Not simply their battle tactics, but their history, philosophy, art.",
           TriggerType.CARD_IN_PLAY, "thrawn", CardType.CHARACTER),
    MARA_JADE(27, "achievement_marajane", "The last command was, 'You will kill Luke Skywalker.'",
              TriggerType.CARD_IN_PLAY, "mara jade", CardType.CHARACTER),
    DASH(28, "achievement_dash", "The name's Dash. Dash Rendar. Freelance.",
         TriggerType.CARD_IN_PLAY, "dash rendar", CardType.CHARACTER),
    XIZOR(29, "achievement_xizor", "Vader will pay for the death of my family.",
          TriggerType.CARD_IN_PLAY, "xizor", CardType.CHARACTER),
    IG88(30, "achievement_ig88", "Bounty hunting is a complicated profession.",
         TriggerType.CARD_IN_PLAY, "ig-88", CardType.CHARACTER),
    DENGAR(31, "achievement_dengar", "I've been waiting for this a long time, Solo.",
           TriggerType.CARD_IN_PLAY, "dengar", CardType.CHARACTER),
    ZUCKUSS(32, "achievement_zuckuss", "The mists have shown me the way.",
            TriggerType.CARD_IN_PLAY, "zuckuss", CardType.CHARACTER),
    FOUR_LOM(33, "achievement_4lom", "Protocol dictates I must inform you: you are worth more dead.",
             TriggerType.CARD_IN_PLAY, "4-lom", CardType.CHARACTER),
    AURRA(34, "achievement_aurra", "I don't work for the Republic.",
          TriggerType.CARD_IN_PLAY, "aurra sing", CardType.CHARACTER),
    CAD_BANE(35, "achievement_cadbane", "I make a living. And I'm the best there is.",
             TriggerType.CARD_IN_PLAY, "cad bane", CardType.CHARACTER),
    GREEDO(36, "achievement_greedo", "Oota goota, Solo?",
           TriggerType.CARD_IN_PLAY, "greedo", CardType.CHARACTER),
    GRIEVOUS(37, "achievement_grievous", "Your lightsabers will make a fine addition to my collection.",
             TriggerType.CARD_IN_PLAY, "grievous", CardType.CHARACTER),
    MACE(38, "achievement_mace", "This party's over.",
         TriggerType.CARD_IN_PLAY, "mace windu", CardType.CHARACTER),
    VENTRESS(39, "achievement_ventress", "I am fear. I am the queen of a blood-soaked planet.",
             TriggerType.CARD_IN_PLAY, "ventress", CardType.CHARACTER),
    REX(40, "achievement_rex", "In my book, experience outranks everything.",
        TriggerType.CARD_IN_PLAY, "captain rex", CardType.CHARACTER),
    CODY(41, "achievement_cody", "Blast him!",
         TriggerType.CARD_IN_PLAY, "commander cody", CardType.CHARACTER),
    PIETT(42, "achievement_piett", "Intensify forward firepower!",
          TriggerType.CARD_IN_PLAY, "piett", CardType.CHARACTER),
    VEERS(43, "achievement_veers", "Maximum firepower!",
          TriggerType.CARD_IN_PLAY, "veers", CardType.CHARACTER),
    PHASMA(44, "achievement_phasma", "You were always scum.",
           TriggerType.CARD_IN_PLAY, "phasma", CardType.CHARACTER),
    HUX(45, "achievement_hux", "Today is the end of the Republic!",
        TriggerType.CARD_IN_PLAY, "general hux", CardType.CHARACTER),
    MANDO(46, "achievement_mando", "This is the way.",
          TriggerType.CARD_IN_PLAY, "din djarin", CardType.CHARACTER),
    GROGU(47, "achievement_grogu", "That's not a toy!",
          TriggerType.CARD_IN_PLAY, "grogu", CardType.CHARACTER),
    GIDEON(48, "achievement_gideon", "You have something I want.",
           TriggerType.CARD_IN_PLAY, "moff gideon", CardType.CHARACTER),
    HERA(49, "achievement_hera", "If all you do is fight for your own life, then your life is worth nothing.",
         TriggerType.CARD_IN_PLAY, "hera syndulla", CardType.CHARACTER),
    EZRA(50, "achievement_ezra", "I'm Ezra Bridger, and this is my home.",
         TriggerType.CARD_IN_PLAY, "ezra bridger", CardType.CHARACTER),
    KANAN(51, "achievement_kanan", "I lost my way for a long time, but now I have a chance to change that.",
          TriggerType.CARD_IN_PLAY, "kanan", CardType.CHARACTER),
    HONDO(52, "achievement_hondo", "This effort is no longer profitable!",
          TriggerType.CARD_IN_PLAY, "hondo", CardType.CHARACTER),

    // =========================================================================
    // Single Card Appearances continued (53-56) - 4 more
    // =========================================================================
    APHRA(53, "achievement_aphra", "I'm not the good guy. Get it through your head.",
          TriggerType.CARD_IN_PLAY, "aphra", CardType.CHARACTER),
    SAW(54, "achievement_saw", "Save the Rebellion! Save the dream!",
        TriggerType.CARD_IN_PLAY, "saw gerrera", CardType.CHARACTER),
    KRENNIC(55, "achievement_krennic", "We were on the verge of greatness. We were this close.",
            TriggerType.CARD_IN_PLAY, "krennic", CardType.CHARACTER),

    // =========================================================================
    // Ships/Locations (56-73) - 18 achievements
    // =========================================================================
    TYDIRIUM(56, "achievement_tydirium", "I was about to clear them.",
             TriggerType.CARD_IN_PLAY, "tydirium", null),
    DEATH_STAR(57, "achievement_deathstar", "That's no moon.",
               TriggerType.CARD_IN_PLAY, "death star", CardType.LOCATION),
    LANDO_FALCON(58, "achievement_lando_falcon", "I'll take good care of her. She won't get a scratch.",
                 TriggerType.CARD_IN_PLAY, "lando in millennium falcon", null),
    ONE_WITH_FORCE(59, "achievement_onewithforce", "I'm one with the Force. The Force is with me.",
                   TriggerType.CARD_IN_PLAY, "chirrut", null),
    EXECUTOR(60, "achievement_executor", "The Emperor is not as forgiving as I am.",
             TriggerType.CARD_IN_PLAY, "executor", null),
    SLAVE_I(61, "achievement_slavei", "Put Captain Solo in the cargo hold.",
            TriggerType.CARD_IN_PLAY, "slave i", null),
    GHOST(62, "achievement_ghost", "Spectre-1, standing by.",
          TriggerType.CARD_IN_PLAY, "ghost", null),
    TANTIVE(63, "achievement_tantive", "There'll be no escape for the Princess this time.",
            TriggerType.CARD_IN_PLAY, "tantive", null),
    OUTRIDER(64, "achievement_outrider", "She may not look like much, but she's got it where it counts.",
             TriggerType.CARD_IN_PLAY, "outrider", null),
    CHIMAERA(65, "achievement_chimaera", "Thrawn's flagship looms overhead.",
             TriggerType.CARD_IN_PLAY, "chimaera", null),
    HOME_ONE(66, "achievement_homeone", "May the Force be with us.",
             TriggerType.CARD_IN_PLAY, "home one", null),
    PROFUNDITY(67, "achievement_profundity", "Rogue One, may the Force be with you.",
               TriggerType.CARD_IN_PLAY, "profundity", null),
    DEVASTATOR(68, "achievement_devastator", "There she is! Set for stun.",
               TriggerType.CARD_IN_PLAY, "devastator", null),
    WILD_KARRDE(69, "achievement_wildkarrde", "Information is the galaxy's most valuable commodity.",
                TriggerType.CARD_IN_PLAY, "wild karrde", null),
    HOUNDS_TOOTH(70, "achievement_houndstooth", "Scorekeeper will be pleased.",
                 TriggerType.CARD_IN_PLAY, "hound's tooth", null),
    SCIMITAR(71, "achievement_scimitar", "At last we will reveal ourselves to the Jedi.",
             TriggerType.CARD_IN_PLAY, "scimitar", null),
    SUPREMACY(72, "achievement_supremacy", "That's Snoke's ship. You think you got him?",
              TriggerType.CARD_IN_PLAY, "supremacy", null),
    STARKILLER(73, "achievement_starkiller", "It's another Death Star.",
               TriggerType.CARD_IN_PLAY, "starkiller base", null),

    // =========================================================================
    // My/Their Card Specific (74-76) - 3 achievements
    // =========================================================================
    FALCON(74, "achievement_falcon", "It's the ship that made the Kessel run in less than 12 parsecs.",
           TriggerType.MY_CARD, "falcon", null),
    BOBA(75, "achievement_boba", "boba fett? boba fett?! where??",
         TriggerType.THEIR_CARD, "boba", null),
    WOMP_RAT(76, "achievement_womprat", "I used to bullseye womp rats in my T-16 back home, they're not much bigger than two meters.",
             TriggerType.THEIR_CARD, "womp rat", null),

    // =========================================================================
    // Card Combinations (77-118) - 42 achievements
    // =========================================================================
    ANAKIN_KENOBI(77, "achievement_anakin_kenobi", "It's Over, Anakin. I Have The High Ground.",
                  TriggerType.CARDS_TOGETHER, null, null, "anakin", "kenobi"),
    EMPEROR_LUKE(78, "achievement_emperor_luke", "Now, young Skywalker, you will die.",
                 TriggerType.CARDS_TOGETHER, null, null, "emperor", "skywalker"),
    LEIA_CHEW(79, "achievement_leia_chew", "Would somebody get this big walking carpet out of my way?",
              TriggerType.CARDS_TOGETHER, null, null, "leia", "chew"),
    KYLO_LUKE(80, "achievement_kylo_luke", "I want every gun that we have to fire on that man.",
              TriggerType.CARDS_TOGETHER, null, null, "kylo ren", "luke skywalker"),
    JABBA_LUKE(81, "achievement_jabba_luke", "Your mind powers won't work on me boy.",
               TriggerType.CARDS_TOGETHER, null, null, "jabba", "luke"),
    ANAKIN_PADME(82, "achievement_anakin_padme", "Ani, you'll always be that little boy I knew on Tatooine.",
                 TriggerType.CARDS_TOGETHER, null, null, "anakin", "luke"),
    TIE_RED5(83, "achievement_tie_red5", "The force is strong with this one.",
             TriggerType.CARDS_TOGETHER, null, null, "custom tie", "red 5"),
    LEIA_TARKIN(84, "achievement_leia_tarkin", "I recognized your foul stench when I was brought onboard.",
                TriggerType.CARDS_TOGETHER, null, null, "tarkin", "leia"),
    LUKE_OWEN(85, "achievement_luke_owen", "But I was going to Tosche Station to pick up some power converters!",
              TriggerType.CARDS_TOGETHER, null, null, "luke", "owen"),
    VADER_MOTTI(86, "achievement_vader_motti", "I find your lack of faith disturbing.",
                TriggerType.CARDS_TOGETHER, null, null, "vader", "motti"),
    C3PO_CRAWLER(87, "achievement_3po_crawler", "What's that, a transport? I'm saved!",
                 TriggerType.CARDS_TOGETHER, null, null, "c-3", "crawler"),
    C3PO_OWEN(88, "achievement_3po_owen", "Shut up, I'll take this one.",
              TriggerType.CARDS_TOGETHER, null, null, "c-3", "owen"),
    C3PO_R2(89, "achievement_3po_r2", "Oh, my dear friend. How I've missed you.",
            TriggerType.CARDS_TOGETHER, null, null, "c-3", "r2-d2"),
    LUKE_OBI(90, "achievement_luke_obi", "The Force will be with you. Always.",
             TriggerType.CARDS_TOGETHER, null, null, "obi", "luke"),
    LEIA_OBI(91, "achievement_leia_obi", "Help me Obi-Wan, you're our only hope.",
             TriggerType.CARDS_TOGETHER, null, null, "leia", "obi"),
    LEIA_LUKE(92, "achievement_leia_luke", "Aren't you a little short for a stormtrooper?",
              TriggerType.CARDS_TOGETHER, null, null, "leia", "luke"),
    VADER_OBI(93, "achievement_vader_obi", "If you strike me down, I shall become more powerful than you can possibly imagine.",
              TriggerType.CARDS_TOGETHER, null, null, "vader", "obi"),
    WERE_HOME(94, "achievement_werehome", "Chewie, we're home.",
              TriggerType.CARDS_TOGETHER, null, null, "chew", "han", "falcon"),
    FETT_LEGACY(95, "achievement_fettlegacy", "I'm just a simple man, like my father before me.",
                TriggerType.CARDS_TOGETHER, null, null, "jango", "boba"),
    FIVE_OH_FIRST(96, "achievement_501st", "We're just clones, sir. We're meant to be expendable.",
                  TriggerType.CARDS_TOGETHER, null, null, "rex", "cody"),
    ORDER_66(97, "achievement_order66", "Execute Order 66.",
             TriggerType.CARDS_TOGETHER, null, null, "cody", "kenobi"),
    SPECTRE(98, "achievement_spectre", "Spectre team, standing by.",
            TriggerType.CARDS_TOGETHER, null, null, "ezra", "hera"),
    MANDALORE(99, "achievement_mandalore", "Wherever I go, he goes.",
              TriggerType.CARDS_TOGETHER, null, null, "din", "grogu"),
    MAUL_KENOBI(100, "achievement_maul_kenobi", "I have been waiting for you.",
                TriggerType.CARDS_TOGETHER, null, null, "maul", "kenobi"),
    DOOKU_ANAKIN(101, "achievement_dooku_anakin", "I've been looking forward to this.",
                 TriggerType.CARDS_TOGETHER, null, null, "dooku", "anakin"),
    GRIEVOUS_KENOBI(102, "achievement_grievous_kenobi", "General Kenobi! You are a bold one.",
                    TriggerType.CARDS_TOGETHER, null, null, "grievous", "kenobi"),
    SHADOWS(103, "achievement_shadows", "Black Sun rises.",
            TriggerType.CARDS_TOGETHER, null, null, "xizor", "guri"),
    BOUNTY_HUNTERS(104, "achievement_bounty_hunters", "We don't need that scum.",
                   TriggerType.CARDS_TOGETHER, null, null, "bossk", "dengar"),
    PROTOCOL_BOUNTY(105, "achievement_protocol_bounty", "An odd couple, but effective.",
                    TriggerType.CARDS_TOGETHER, null, null, "4-lom", "zuckuss"),
    INQUISITORS(106, "achievement_inquisitors", "There are some things far more frightening than death.",
                TriggerType.CARDS_TOGETHER, null, null, "inquisitor", "fifth brother"),
    TWIN_SUNS(107, "achievement_twin_suns", "Look sir, droids!",
              TriggerType.CARDS_TOGETHER, null, null, "r2", "c-3"),
    STOMPY(108, "achievement_stompy", "Imperial walkers on the north ridge!",
           TriggerType.CARDS_TOGETHER, null, null, "blizzard 1", "blizzard 2"),
    WALKER_ASSAULT(109, "achievement_walker_assault", "That armor's too strong for blasters.",
                   TriggerType.CARDS_TOGETHER, null, null, "blizzard", "blizzard 4"),
    BOUNTY_FLEET(110, "achievement_bounty_fleet", "There will be a substantial reward.",
                 TriggerType.CARDS_TOGETHER, null, null, "slave i", "hound's tooth"),
    HUNTER_ARMADA(111, "achievement_hunter_armada", "No disintegrations.",
                  TriggerType.CARDS_TOGETHER, null, null, "mist hunter", "punishing one"),
    ROGUE_SQUADRON(112, "achievement_rogue_squadron", "Lock S-foils in attack position.",
                   TriggerType.CARDS_TOGETHER, null, null, "red 5", "red leader"),
    GOLD_SQUADRON(113, "achievement_gold_squadron", "Stay on target!",
                  TriggerType.CARDS_TOGETHER, null, null, "gold leader", "gold 1"),
    IMPERIAL_NAVY(114, "achievement_imperial_navy", "Concentrate all fire on that Super Star Destroyer!",
                  TriggerType.CARDS_TOGETHER, null, null, "executor", "chimaera"),
    BLOCKADE(115, "achievement_blockade", "A communications disruption can only mean one thing.",
             TriggerType.CARDS_TOGETHER, null, null, "blockade flagship", "droid control"),
    CLOUD_CARS(116, "achievement_cloud_cars", "I've just made a deal that will keep the Empire out of here forever.",
               TriggerType.CARDS_TOGETHER, null, null, "cloud car", "combat cloud car"),

    // =========================================================================
    // Location-Specific Combos (117-118) - 2 achievements
    // =========================================================================
    LEIA_HAN_HOTH(117, "achievement_leia_han_hoth", "Why, you stuck-up, half-witted, scruffy-looking nerf herder!",
                  TriggerType.CARDS_AT_SITE, null, null, "hoth", "leia", "han"),
    SAND(118, "achievement_sand", "I Don't Like Sand. It's Coarse And Rough And Irritating. And It Gets Everywhere.",
         TriggerType.CARDS_AT_SITE, null, null, "tatooine", "anakin"),

    // =========================================================================
    // Combat/Damage (119-122) - 4 achievements
    // =========================================================================
    SIXTY_DAMAGE(119, "achievement_60_damage", "We seem to be made to suffer. It's our lot in life.",
                 TriggerType.DAMAGE, null, null, 60),
    R2_KILLED(120, "achievement_r2_killed", "We're doomed.",
              TriggerType.CARD_KILLED, "r2-d2", null),
    CHEWIE_KILLED(121, "achievement_chewie_killed", "Will somebody get this big walking carpet out of my way.",
                  TriggerType.CARD_KILLED, "chew", null),
    HAN_BOBA(122, "achievement_han_boba", "He's no good to me dead.",
             TriggerType.CARD_KILLED_BY, "han", null, "boba"),

    // =========================================================================
    // Route Score Achievements (123-126) - 4 achievements
    // =========================================================================
    PERFECT_ROUTE(123, "achievement_perfect_route", "A hyperspace route this good could fund a rebellion!",
                  TriggerType.ROUTE_SCORE, null, null, 50),
    FIRST_SELLABLE(124, "achievement_first_sellable", "Your first sellable route! I knew you had potential.",
                   TriggerType.FIRST_ROUTE_SCORE, null, null, 30),
    COMEBACK(125, "achievement_comeback", "From the brink of failure to profit!",
             TriggerType.COMEBACK, null, null, 30),
    SPEEDRUN(126, "achievement_speedrun", "The Kessel Run has nothing on this!",
             TriggerType.SPEEDRUN, null, null, 5),

    // =========================================================================
    // Gameplay Achievements (127-131) - 5 achievements
    // =========================================================================
    PACIFIST(127, "achievement_pacifist", "Violence is never the answer... apparently.",
             TriggerType.PACIFIST, null, null),
    BLITZKRIEG(128, "achievement_blitzkrieg", "Aggressive negotiations complete.",
               TriggerType.BATTLES_WON, null, null, 3),
    FORTRESS(129, "achievement_fortress", "The galaxy trembles at your dominance.",
             TriggerType.LOCATIONS_CONTROLLED, null, null, 5),
    ECONOMIST(130, "achievement_economist", "A credit saved is a credit earned.",
              TriggerType.FORCE_REMAINING, null, null, 15),
    COLLECTOR(131, "achievement_collector", "Impressive collection you have there.",
              TriggerType.HAND_SIZE, null, null, 8),

    // =========================================================================
    // Meta Achievements (132-136) - 5 achievements
    // =========================================================================
    REGULAR(132, "achievement_regular", "A regular customer! The droid remembers you.",
            TriggerType.GAMES_PLAYED, null, null, 10),
    VETERAN(133, "achievement_veteran", "You've logged more hyperspace hours than most pilots.",
            TriggerType.GAMES_PLAYED, null, null, 50),
    LEGEND(134, "achievement_legend", "Your name echoes across the trade routes.",
           TriggerType.GAMES_PLAYED, null, null, 100),
    PERFECTIONIST(135, "achievement_perfectionist", "Achievement unlocked: achievement unlocker.",
                  TriggerType.ACHIEVEMENTS, null, null, 50),
    HIGH_ROLLER(136, "achievement_highroller", "The traders speak your name with reverence.",
                TriggerType.AST_SCORE, null, null, 500),

    // =========================================================================
    // Seasonal/Random (137-138) - 2 achievements
    // =========================================================================
    LUCKY(137, "achievement_lucky", "Never tell me the odds!",
          TriggerType.LUCKY_WIN, null, null),
    UNLUCKY(138, "achievement_unlucky", "Even droids feel sympathy sometimes.",
            TriggerType.UNLUCKY_LOSS, null, null),

    // =========================================================================
    // Holiday Achievements - Life Day (139-143) - 5 achievements
    // Only available during December
    // =========================================================================
    LIFE_DAY_CELEBRANT(139, "achievement_life_day_celebrant", "Happy Life Day! You've celebrated the Wookiee way.",
                       TriggerType.HOLIDAY_GAME_COMPLETE, null, null),
    WOOKIEE_HOMECOMING(140, "achievement_wookiee_homecoming", "Chewie made it home for Life Day! Malla, Itchy, and Lumpy are overjoyed!",
                       TriggerType.CARDS_AT_SITE, null, null, "kashyyyk", "chew"),
    FIRST_BOBA(141, "achievement_first_boba", "Boba Fett! His first appearance was in the Holiday Special. Yes, really.",
               TriggerType.HOLIDAY_CARD_IN_PLAY, "boba", null),
    LIFE_DAY_ORB(142, "achievement_life_day_orb", "You hold the Life Day orb aloft! The Tree of Life glows with approval.",
                 TriggerType.HOLIDAY_SELLABLE_ROUTE, null, null, 30),
    HOLIDAY_SPECIAL(143, "achievement_holiday_special", "You've watched... I mean PLAYED through the Holiday Special! All of it!",
                    TriggerType.HOLIDAY_GAMES_PLAYED, null, null, 3);

    // Total: 144 achievements (bits 0-143, requiring 18 bytes)
    public static final int TOTAL_ACHIEVEMENTS = 144;
    public static final int BYTES_REQUIRED = 18; // 144 bits = 18 bytes

    private final int _bitPosition;
    private final String _key;
    private final String _quote;
    private final TriggerType _triggerType;
    private final String _cardMatch;
    private final CardType _cardType;
    private final Integer _threshold;
    private final List<String> _cards;
    private final String _siteFilter;

    /**
     * Constructor for single card achievements.
     */
    Achievement(int bitPosition, String key, String quote, TriggerType triggerType,
                String cardMatch, CardType cardType) {
        _bitPosition = bitPosition;
        _key = key;
        _quote = quote;
        _triggerType = triggerType;
        _cardMatch = cardMatch != null ? cardMatch.toLowerCase() : null;
        _cardType = cardType;
        _threshold = null;
        _cards = Collections.emptyList();
        _siteFilter = null;
    }

    /**
     * Constructor for card combo achievements.
     */
    Achievement(int bitPosition, String key, String quote, TriggerType triggerType,
                String cardMatch, CardType cardType, String... cards) {
        _bitPosition = bitPosition;
        _key = key;
        _quote = quote;
        _triggerType = triggerType;
        _cardMatch = cardMatch != null ? cardMatch.toLowerCase() : null;
        _cardType = cardType;
        _threshold = null;
        _cards = Arrays.asList(cards);
        // First card might be site filter for CARDS_AT_SITE
        if (triggerType == TriggerType.CARDS_AT_SITE && cards.length > 0) {
            _siteFilter = cards[0].toLowerCase();
        } else {
            _siteFilter = null;
        }
    }

    /**
     * Constructor for threshold-based achievements.
     */
    Achievement(int bitPosition, String key, String quote, TriggerType triggerType,
                String cardMatch, CardType cardType, int threshold) {
        _bitPosition = bitPosition;
        _key = key;
        _quote = quote;
        _triggerType = triggerType;
        _cardMatch = cardMatch != null ? cardMatch.toLowerCase() : null;
        _cardType = cardType;
        _threshold = threshold;
        _cards = Collections.emptyList();
        _siteFilter = null;
    }

    /**
     * Constructor for card killed by achievements.
     */
    Achievement(int bitPosition, String key, String quote, TriggerType triggerType,
                String cardMatch, CardType cardType, String killerCard) {
        _bitPosition = bitPosition;
        _key = key;
        _quote = quote;
        _triggerType = triggerType;
        _cardMatch = cardMatch != null ? cardMatch.toLowerCase() : null;
        _cardType = cardType;
        _threshold = null;
        _cards = Collections.singletonList(killerCard.toLowerCase());
        _siteFilter = null;
    }

    public int getBitPosition() {
        return _bitPosition;
    }

    public String getKey() {
        return _key;
    }

    public String getQuote() {
        return _quote;
    }

    public TriggerType getTriggerType() {
        return _triggerType;
    }

    public String getCardMatch() {
        return _cardMatch;
    }

    public CardType getCardType() {
        return _cardType;
    }

    public Integer getThreshold() {
        return _threshold;
    }

    public List<String> getCards() {
        return _cards;
    }

    public String getSiteFilter() {
        return _siteFilter;
    }

    /**
     * Check if this is a holiday-only achievement.
     */
    public boolean isHolidayAchievement() {
        return _triggerType == TriggerType.HOLIDAY_GAME_COMPLETE ||
               _triggerType == TriggerType.HOLIDAY_CARD_IN_PLAY ||
               _triggerType == TriggerType.HOLIDAY_SELLABLE_ROUTE ||
               _triggerType == TriggerType.HOLIDAY_GAMES_PLAYED ||
               _key.contains("life_day") || _key.contains("wookiee_homecoming") ||
               _key.contains("first_boba") || _key.contains("holiday_special");
    }

    /**
     * Find achievement by key.
     */
    public static Achievement findByKey(String key) {
        for (Achievement ach : values()) {
            if (ach.getKey().equals(key)) {
                return ach;
            }
        }
        return null;
    }

    /**
     * Find achievement by bit position.
     */
    public static Achievement findByBitPosition(int bitPosition) {
        for (Achievement ach : values()) {
            if (ach.getBitPosition() == bitPosition) {
                return ach;
            }
        }
        return null;
    }

    /**
     * Card type filter for achievements.
     */
    public enum CardType {
        CHARACTER,
        STARSHIP,
        VEHICLE,
        WEAPON,
        DEVICE,
        EFFECT,
        INTERRUPT,
        LOCATION
    }

    /**
     * Achievement trigger types.
     */
    public enum TriggerType {
        CARD_IN_PLAY,
        MY_CARD,
        THEIR_CARD,
        CARDS_TOGETHER,
        CARDS_AT_SITE,
        CARD_KILLED,
        CARD_KILLED_BY,
        DAMAGE,
        ROUTE_SCORE,
        FIRST_ROUTE_SCORE,
        COMEBACK,
        SPEEDRUN,
        PACIFIST,
        BATTLES_WON,
        LOCATIONS_CONTROLLED,
        FORCE_REMAINING,
        HAND_SIZE,
        GAMES_PLAYED,
        ACHIEVEMENTS,
        AST_SCORE,
        LUCKY_WIN,
        UNLUCKY_LOSS,
        HOLIDAY_GAME_COMPLETE,
        HOLIDAY_CARD_IN_PLAY,
        HOLIDAY_SELLABLE_ROUTE,
        HOLIDAY_GAMES_PLAYED
    }
}
