package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines the base implementation of a permanent built-in.
 */
public abstract class AbstractPermanent implements SwccgBuiltInCardBlueprint {
    private Integer _permCardId;
    private int _builtInId;
    private String _title;
    private String _crossedOverTitle;
    private Uniqueness _uniqueness;
    private Set<Keyword> _keywords = new HashSet<Keyword>();
    private Set<Persona> _personas = new HashSet<Persona>();

    /**
     * Creates the base implementation of a permanent built-in.
     * @param title the title
     * @param crossedOverTitle the title if the card is crossed over
     * @param uniqueness the uniqueness
     */
    protected AbstractPermanent(String title, String crossedOverTitle, Uniqueness uniqueness) {
        _title = title;
        _crossedOverTitle = crossedOverTitle;
        _uniqueness = uniqueness;
    }

    /**
     * Sets the card the permanent built-in is on.
     * @param card the card the permanent build-in is on
     */
    @Override
    public void setPhysicalCard(PhysicalCard card) {
        if (card != null) {
            _permCardId = card.getPermanentCardId();
        }
    }

    /**
     * Gets the card the permanent built-in is on.
     * @param game the game
     * @return the card the permanent built-in is on
     */
    @Override
    public PhysicalCard getPhysicalCard(SwccgGame game) {
        return game.getGameState().findCardByPermanentId(_permCardId);
    }

    /**
     * Sets the id of the permanent built-in.
     * @param builtInId the id of the permanent built-in
     */
    @Override
    public void setBuiltInId(int builtInId) {
        _builtInId = builtInId;
    }

    /**
     * Gets the id of the permanent built-in.
     * @return the id of the permanent built-in
     */
    @Override
    public int getBuiltInId() {
        return _builtInId;
    }

    /**
     * Gets the title of the permanent built-in.
     * @param game the game
     * @return the uniqueness
     */
    @Override
    public final String getTitle(SwccgGame game) {
        if (game != null) {
            PhysicalCard card = game.getGameState().findCardById(_permCardId);
            if (card != null && card.isCrossedOver()) {
                return _crossedOverTitle;
            }
        }
        return _title;
    }

    /**
     * Gets the uniqueness of the permanent built-in.
     * @return the uniqueness
     */
    @Override
    public final Uniqueness getUniqueness() {
        return _uniqueness;
    }

    /**
     * Adds the specified keywords.
     * @param keywords the keywords
     */
    protected final void addKeywords(Keyword... keywords) {
        for (Keyword keyword : keywords)
            addKeyword(keyword);
    }

    /**
     * Adds the specified keyword.
     * @param keyword the keyword
     */
    public final void addKeyword(Keyword keyword) {
        _keywords.add(keyword);
    }

    /**
     * Determines if the permanent built-in has the specified keyword.
     * @param keyword the keyword
     * @return true or false
     */
    @Override
    public boolean hasKeyword(Keyword keyword) {
        return _keywords.contains(keyword);
    }

    /**
     * Adds the specified personas.
     * @param personas the personas
     */
    protected final void addPersonas(Persona... personas) {
        for (Persona persona : personas)
            addPersona(persona);
    }

    /**
     * Adds the specified persona.
     * @param persona the personas
     */
    protected final void addPersona(Persona persona) {
        _personas.add(persona);
    }

    /**
     * Determines if the permanent built-in has the specified persona.
     * @param game the game
     * @param persona the persona
     * @return true or false
     */
    @Override
    public final boolean hasPersona(SwccgGame game, Persona persona) {
        return getPersonas(game).contains(persona);
    }

    /**
     * Gets the persons that the built-in has.
     * @param game the game
     * @return the personas
     */
    @Override
    public final Set<Persona> getPersonas(SwccgGame game) {
        if (game != null) {
            PhysicalCard card = game.getGameState().findCardByPermanentId(_permCardId);
            if (card != null) {
                if ((card.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                        || card.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
                        && card.isStolen()) {
                    return Collections.emptySet();
                }
                if (card.isCrossedOver()) {
                    Set<Persona> crossedOverPersonas = new HashSet<Persona>();
                    for (Persona persona : _personas) {
                        crossedOverPersonas.add(persona.getCrossedOverPersona());
                    }
                    return crossedOverPersonas;
                }
            }
        }
        return _personas;
    }

    /**
     * Gets modifiers generated from the built-in.
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
        return null;
    }

    /**
     * Determines if the built-in is a permanent weapon.
     * @return true or false
     */
    @Override
    public boolean isWeapon() {
        return false;
    }

    /**
     * Determines if the built-in is a permanent pilot.
     * @return true or false
     */
    @Override
    public boolean isPilot() {
        return false;
    }

    /**
     * Determines if the built-in is a permanent astromech.
     * @return true or false
     */
    @Override
    public boolean isAstromech() {
        return false;
    }

    /**
     * Gets the ability of the built-in.
     * @return the ability
     */
    @Override
    public float getAbility() {
        throw new UnsupportedOperationException("This method, getAbility(), should not be called on this build-in: " + _title);
    }

    /**
     * Gets the fire weapon actions for each way the permanent weapon can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played     @return the fire weapon actions
     * @param ignorePerAttackOrBattleLimit
     */
    @Override
    public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        throw new UnsupportedOperationException("This method, getGameTextFireWeaponActions(), should not be called on this build-in: " + _title);
    }

    @Override
    public List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        throw new UnsupportedOperationException("This method, getGameTextFireWeaponActions(), should not be called on this build-in: " + _title);
    }
}
