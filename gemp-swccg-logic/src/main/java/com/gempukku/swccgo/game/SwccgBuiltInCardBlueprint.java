package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;
import java.util.Set;

/**
 * Defines the methods that card built-in (permanent pilots, permanent astromechs, and permanent weapons) must implement.
 */
public interface SwccgBuiltInCardBlueprint {

    /**
     * Sets the card the permanent built-in is on.
     * @param card the card the permanent build-in is on
     */
    void setPhysicalCard(PhysicalCard card);

    /**
     * Gets the card the permanent built-in is on.
     * @param game the game
     * @return the card the permanent built-in is on
     */
    PhysicalCard getPhysicalCard(SwccgGame game);

    /**
     * Sets the id of the permanent built-in.
     * @param builtInId the id of the permanent built-in
     */
    void setBuiltInId(int builtInId);

    /**
     * Gets the id of the permanent built-in.
     * @return the id of the permanent built-in
     */
    int getBuiltInId();

    /**
     * Gets the uniqueness of the permanent built-in.
     * @return the uniqueness
     */
    Uniqueness getUniqueness();

    /**
     * Gets the title of the permanent built-in.
     * @param game the game
     * @return the uniqueness
     */
    String getTitle(SwccgGame game);

    /**
     * Determines if the permanent built-in has the specified keyword.
     * @param keyword the keyword
     * @return true or false
     */
    boolean hasKeyword(Keyword keyword);

    /**
     * Determines if the permanent built-in has the specified persona.
     * @param game the game
     * @param persona the persona
     * @return true or false
     */
    boolean hasPersona(SwccgGame game, Persona persona);

    /**
     * Gets the persons that the built-in has.
     * @param game the game
     * @return the personas
     */
    Set<Persona> getPersonas(SwccgGame game);

    /**
     * Determines if the built-in is a permanent weapon.
     * @return true or false
     */
    boolean isWeapon();

    /**
     * Determines if the built-in is a permanent pilot.
     * @return true or false
     */
    boolean isPilot();

    /**
     * Determines if the built-in is a permanent astromech.
     * @return true or false
     */
    boolean isAstromech();

    /**
     * Gets the ability of the built-in.
     * @return the ability
     */
    float getAbility();

    /**
     * Gets modifiers generated from the built-in.
     * @param self the card
     * @return the modifiers
     */
    List<Modifier> getGameTextModifiers(PhysicalCard self);

    /**
     * Gets the fire weapon actions for each way the permanent weapon can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the fire weapon actions
     */
    List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit);
}
