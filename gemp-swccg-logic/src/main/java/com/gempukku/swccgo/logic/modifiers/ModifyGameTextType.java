package com.gempukku.swccgo.logic.modifiers;

/**
 * This enum represents the different modifications of game text the involve cards affecting other specific cards in
 * specific ways. Only use these if there is not a reasonable way to have the change be handled in a more general way by
 * the game engine.
 */
public enum ModifyGameTextType {

    // Game text modifier flags
    // (Naming convention: CardIdentifier__ModificationIdentifier)
    A_POWER_LOSS__CARDS_GO_LOST_INSTEAD_OF_USED("Cards from A Power Loss go to owner’s Lost Pile instead of Used Pile"),
    ALWAYS_THINKING_WITH_YOUR_STOMACH__MISSING_TREATED_AS_LANDSPEED_0("'Missing' treated as 'landspeed = 0 for remainder of turn"),
    BOONTA_EVE_PODRACE__RETRIEVE_FORCE_INTO_HAND("Take any or all force retrieved into hand"),
    BRING_HIM_BEFORE_ME__MAY_NOT_CAPTURE_LUKE("May not capture Luke"),
    BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE("Targets Leia instead of Luke"),
    COUNTER_ASSAULT__ADD_DESTINY_TO_TOTAL("Add one destiny to total"),
    DARK_DEAL__ADDITIONAL_BESPIN_LOCATION_TO_CANCEL("Additional Bespin location required to cancel"),
    DEATH_STAR__MAY_DEPLOY_WITHOUT_COMPLETING_DEATH_STAR_PLANS("May deploy without completing Death Star Plans"),
    DEATH_STAR_PLANS__ADD_DESTINY_TO_FORCE_RETRIEVED("Add one destiny to Force retrieved"),
    DEATH_STAR_SENTRY__APPLIES_ALL_MODIFIERS("Applies all modifiers"),
    DO_THEY_HAVE_A_CODE_CLEARANCE__DOESNT_MODIFY_FORFEIT("Does not modify Imperials' forfeit."),
    DONT_UNDERESTIMATE_OUR_CHANCES__TRIPLE_RESULT("Triple result"),
    EBO__ADDITIONAL_SITE_TO_CANCEL("Opponent must occupy additional Hoth site to cancel"),
    ELLORRS_MADAK__ADDITIONAL_2_TO_POWER_BONUS("Additional 2 to power bonus"),
    FEAR_WILL_KEEP_THEM_IN_LINE__ADDS_1_TO_ATTRITION("Add 1 to attrition"),
    FLAGSHIP_OPERATIONS__MAY_IGNORE_DEPLOYMENT_RESTRICTIONS("May ignore deployment restrictions"),
    GRIMTAASH__PUT_TWO_CARDS_IN_USED("Place two cards in used pile first"),
    HOTH_SENTRY__APPLIES_ALL_MODIFIERS("Applies all modifiers"),
    HUNT_DOWN__DO_NOT_PLACE_OUT_OF_PLAY_IF_MAUL_DUELS("Not placed out of play if Maul initiates a duel"),
    I_CANT_BELIEVE_HES_GONE__ONLY_EFFECTS_BATTLES_WITH_LUKE_OR_LEIA("Only applies to battles with Luke or Leia"),
    IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5("Stack destiny card on Jedi Test #5"),
    IMPERIAL_DECREE__DOES_NOT_COUNT_YAVIN_4_LOCATIONS("Does not count Yavin 4 locations"),
    LEIA_JABBAS_PALACE__TARGET_WARRIOR_AT_AUDIENCE_CHAMBER_INSTEAD_OF_JABBA("[Jabba's Palace] Leia may target a warrior at Audience Chamber instead of Jabba"),
    LET_THEM_MAKE_THE_FIRST_MOVE__ONLY_TARGET_UNDERCOVER_SPIES_AND_R2D2("May target only Undercover spies and R2-D2"),
    LOST_IN_THE_WILDERNESS__MISSING_TREATED_AS_LANDSPEED_0("'Missing' treated as 'landspeed = 0 for remainder of turn"),
    JAWA_SIESTA__DOUBLED_BY_KALIT("Doubled And May Deploy For Free"),
    JAWA_PACK__DOUBLED_BY_WITTIN("Doubled And May Deploy For Free"),
    KETWOL__MAY_EXCHANGE_DOCKING_BAY_ONCE_PER_GAME("May exchange docking bay only once per game"),
    MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE("Targets Leia instead of Luke"),
    MONNOK__PUT_TWO_CARDS_IN_USED("Place two cards in used pile first"),
    NABRUN_LEIDS_ELIS_HELROT__LIMIT_USAGE("Limited to owner's move phase and exterior sites"),
    PLASTOID_ARMOR__CHANGE_DEPLOYMENT("Deploys on a Rebel or alien at same mobile site as Elom"),
    PROPHECY_OF_THE_FORCE__MAY_NOT_BE_RELOCATED("May not be relocated"),
    RADAR_SCANNER__JAWAS_TUSKEN_RAIDERS_AND_STORMTROOPERS_LOST("Jawas, Tusken Raiders, and stormtroopers lost"),
    REBEL_PLANNERS__APPLIES_TO_EVERY_SYSTEM("Applies to every system"),
    REBEL_TECH__DOUBLE_BONUS_TO_ATTACK_RUN("Attack Run bonus doubled"),
    RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT("May not be played (except to cancel opponent's Interrupt)"),
    REFLECTIONS_II_OBJECTIVE__TARGETS_REY_INSTEAD_OF_LUKE("Targets Rey instead of Luke"),
    RESCUE_THE_PRINCESS__CANNOT_BE_PLACED_OUT_OF_PLAY("Cannot be placed out of play"),
    SAVE_YOU_IT_CAN__MOVE_PHASE_MAY_BE_TREATED_AS_DEPLOY_PHASE("'Move phase' may be treated as 'deploy phase'"),
    SCANNING_CREW__CARDS_WITH_REBEL_IN_TITLE_LOST("Cards with 'Rebel' in title lost"),
    SET_YOUR_COURSE_FOR_ALDERAAN__ONLY_AFFECTS_DARK_SIDE_DEATH_STAR_SITES("Only affects Dark Side Death Star sites"),
    SHOT_IN_THE_DARK__LOSE_ADDITIONAL_FORCE_TO_DRAW("Must lose an additional 1 Force to draw a card"),
    SOLO__MAY_NOT_PLAY_INTERRUPT_FROM_LOST_PILE("May not play interrupt from lost pile"),
    SORRY_ABOUT_THE_MESS__WEAPONS_FIRED_MUST_TARGET_GREEDO_IF_POSSIBLE("Weapons fired must target Greedo (if possible)"),
    SPACEPORT_SPEEDERS_CAN_BE_PLAYED_AT_DROID_MERCHANTS_LOCATION("Spaceport Speeders may be played at Droid Merchant's site."),
    SPECIAL_DELIVERY__TAKE_TWO_ADDITIONAL_CARDS_INTO_HAND("Take two additional cards into hand"),
    SPECIAL_MODIFICATIONS__IMMUNE_TO_ATTRITION_LESS_THAN_FOUR("Makes target to attrition < 4"),
    SPICE_MINES_OF_KESSEL__ADD_4_TO_FORCE_RETRIEVED("Add 4 to Force retrieved"),
    SUPERLASER_IGNORES_DEPLOYMENT_RESTRICTIONS("Ignores deployment restrictions"),
    SURPRISE_ASSAULT__ADD_DESTINY_TO_TOTAL("Add one destiny to total"),
    TALLON_ROLL__OPPONENT_ADDS_MANEUVER_AND_ABILITY("Opponent adds maneuver and ability"),
    TARKIN__CANNOT_CANCEL_DESTINY("Cannot cancel destiny"),
    TIBANNA_GAS_MINER__DOUBLE_FORCE_ACTIVATED("Force activated doubled"),
    TRAP_DOOR__DO_NOT_DRAW_DESTINY("Do not draw destiny"),
    TUSKEN_BREATH_MASK__MODIFIED_BY_SERGEANT_DOALLYN("Game text modified"),
    TUSKEN_SCAVENGERS__MAY_STEAL_CARDS_FOUND("May steal vehicles, weapons, and devices found"),
    UGNAUGHT__DOUBLE_CARBON_FREEZING_DESTINY_BONUS("Carbon-Freezing destiny bonus doubled"),
    UNCERTAIN_IS_THE_FUTURE__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT("May not be played (except to cancel opponent's Interrupt)"),
    WALKER_GARRISON__ADDITIONAL_SITE_TO_GAIN_FORCE_DRAIN_BONUS("Additional site required for Force drain bonus"),
    WATTOS_BOX__MAY_DEPLOY_REGARDLESS_OF_RACE_TOTAL("May deploy regardless of race total"),
    WELL_HANDLE_THIS__ONLY_TARGET_UNDERCOVER_SPIES_AND_5D6RA7("May target only Undercover spies and 5D6-RA-7"),
    VADER__DOES_NOT_ADD_1_TO_BATTLE_DESTINY("Does not add 1 to battle destiny"),
    YAVIN_SENTRY__APPLIES_ALL_MODIFIERS("Applies all modifiers"),
    YOU_CAN_EITHER_PROFIT_BY_THIS__DO_NOT_DEPLOY_HAN_AT_START_OF_GAME("Do not deploy Han at start of game"),
    YOU_OVERESTIMATE_THEIR_CHANCES__TRIPLE_RESULT("Triple result"),
    YOURE_A_SLAVE__DRAW_TOP_CARD_OF_RESERVE_DECK_WHEN_PLACING_A_CARD_IN_USED_PILE("Draw top card of Reserve Deck when you place a card in Used Pile."),


    //Legacy
    LEGACY__YOUR_SITES__TREAT_TRANDOSHAN_AS_SLAVER("'Trandoshan' on this site may be treated as 'slaver'"),
    LEGACY__REF_III_ANOTHER_PATHETIC_LIFEFORM__IGNORES_YOUR_NONUNIQUE_ALIENS("Ignores non-unique aliens"),
    LEGACY__OUTRIDER_DOES_NOT_PLACE_UTINNI_EFFECTS_OUT_OF_PLAY("Does not place Utinni Effects out of play"),
    LEGACY__WELL_HANDLE_THIS__ONLY_TARGET_DROIDS_AND_SPIES("May target only droids and spies"),
    LEGACY__LET_THEM_MAKE_THE_FIRST_MOVE__ONLY_TARGET_DROIDS_AND_SPIES("May target only droids and spies"),

    ;

    private String _humanReadable;

    ModifyGameTextType(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

}