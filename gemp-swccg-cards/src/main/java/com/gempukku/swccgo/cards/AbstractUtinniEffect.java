package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.logic.timing.rules.UtinniEffectRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * The abstract class providing the common implementation for Utinni Effects.
 */
public abstract class AbstractUtinniEffect extends AbstractEffect {

    /**
     * Creates a blueprint for an Utinni Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractUtinniEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title) {
        this(side, destiny, playCardZoneOption, title, null);
    }

    /**
     * Creates a blueprint for an Utinni Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractUtinniEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness) {
        this(side, destiny, playCardZoneOption, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for an Utinni Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractUtinniEffect(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.UTINNI);
    }

    /**
     * Gets the valid deploy target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can deploy to.
     *
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param isSimDeployAttached true if during simultaneous deployment of pilot/weapon, otherwise false
     * @param ignorePresenceOrForceIcons true if this deployment ignores presence or Force icons requirement
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @return the deploy to target filter based on the card type, subtype, etc.
     */
    @Override
    protected final Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        Filter filter = super.getValidDeployTargetFilterForCardType(playerId, game, self, isSimDeployAttached, ignorePresenceOrForceIcons, deploymentRestrictionsOption, deployAsCaptiveOption);
        if (!isDagobahAllowed()) {
            filter = Filters.and(filter, Filters.not(Filters.locationAndCardsAtLocation(Filters.Dagobah_location)));
        }
        return filter;
    }

    /**
     * Gets the Utinni Effect target ids used by the Utinni Effect.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the Utinni Effect target ids
     */
    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(TargetId.UTINNI_EFFECT_TARGET_1);
    }

    /**
     * Gets a filter for the cards the specified Utinni Effect may target (not including to deploy on).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Utinni Effect on
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    @Override
    public final Filter getValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        Filter filter = Filters.and(Filters.canBeTargetedBy(self), getGameTextValidUtinniEffectTargetFilter(playerId, game, self, deployTarget, targetId));
        if (!isDagobahAllowed()) {
            filter = Filters.and(filter, Filters.not(Filters.locationAndCardsAtLocation(Filters.Dagobah_location)));
        }
        return filter;
    }

    /**
     * Gets the valid target filter that the specified Utinni Effect may remain targeting. If the card targeted by the Utinni
     * Effect becomes not accepted by this filter, then the Utinni Effect will be lost by rule.
     * @param game the game
     * @param self the card
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    @Override
    public final Filter getValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.and(getGameTextValidUtinniEffectTargetFilterToRemainTargeting(game, self, targetId), Filters.canBeTargetedBy(self));
    }

    /**
     * Gets effects (to be performed in order) that set any targeted cards when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        List<TargetingEffect> targetingEffects = new LinkedList<TargetingEffect>();

        for (final TargetId targetId : getUtinniEffectTargetIds(playerId, game, self)) {
            final Filter targetFilter = getValidUtinniEffectTargetFilter(playerId, game, self, target, targetId);
            Map<InactiveReason, Boolean> spotOverrides = getTargetSpotOverride(targetId);

            TargetingEffect targetingEffect = new TargetCardOnTableEffect(action, playerId, "Choose target", spotOverrides, targetFilter) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                    action.addAnimationGroup(target);
                    self.setTargetedCard(targetId, targetGroupId, target, targetFilter);
                }
            };
            targetingEffects.add(targetingEffect);
        }
        return targetingEffects;
    }

    /**
     * This method if overridden by individual cards to get effects (to be performed in order) that set any targeted cards
     * when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    protected final List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }

    /**
     * Determines if the Utinni Effect is allowed to deploy or target cards at Dagobah locations.
     * @return true or false
     */
    protected boolean isDagobahAllowed() {
        return false;
    }

    /**
     * Gets modifiers from the card that are in effect while the card is in play (unless game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getWhileInPlayModifiers(game, self);

        // Set persistent, so these modifiers are in effect even when cards text is suspended/canceled
        final int permCardId = self.getPermanentCardId();
        Modifier modifier = new SuspendsCardModifier(self, self,
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        // Suspend the Utinni Effect if any of its targets are inactive
                        for (TargetId targetId : self.getBlueprint().getUtinniEffectTargetIds(self.getOwner(), game, self)) {
                            for (PhysicalCard targetCard : Filters.filterAllOnTable(game, Filters.targetedByCardOnTableAsTargetId(self, targetId))) {

                                Map<InactiveReason, Boolean> spotOverride = getTargetSpotOverride(targetId);
                                if (!gameState.isCardInPlayActive(targetCard,
                                        spotOverride != null && spotOverride.get(InactiveReason.EXCLUDED_FROM_BATTLE) != null && spotOverride.get(InactiveReason.EXCLUDED_FROM_BATTLE),
                                        spotOverride != null && spotOverride.get(InactiveReason.UNDERCOVER) != null && spotOverride.get(InactiveReason.UNDERCOVER),
                                        spotOverride != null && spotOverride.get(InactiveReason.CAPTIVE) != null && spotOverride.get(InactiveReason.CAPTIVE),
                                        spotOverride != null && spotOverride.get(InactiveReason.CONCEALED) != null && spotOverride.get(InactiveReason.CONCEALED),
                                        spotOverride != null && spotOverride.get(InactiveReason.STOLEN_WEAPON_DEVICE) != null && spotOverride.get(InactiveReason.STOLEN_WEAPON_DEVICE),
                                        spotOverride != null && spotOverride.get(InactiveReason.MISSING) != null && spotOverride.get(InactiveReason.MISSING),
                                        spotOverride != null && spotOverride.get(InactiveReason.TURNED_OFF) != null && spotOverride.get(InactiveReason.TURNED_OFF),
                                        spotOverride != null && spotOverride.get(InactiveReason.SUSPENDED) != null && spotOverride.get(InactiveReason.SUSPENDED))) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                }) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
                return self.isSuspended() ? "Suspended due to inactive Utinni Effect target" : null;
            }};
        modifier.setPersistent(true);
        modifiers.add(modifier);
        return modifiers;
    }

    /**
     * This method is overridden by individual cards to specify the filter for valid Utinni Effect targets.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Utinni Effect on
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.any;
    }

    /**
     * This method is overridden by individual cards to specify the filter that Utinni Effect targets must be accepted by
     * or the Utinni Effect is lost by rule.
     * @param game the game
     * @param self the card
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.any;
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<TriggerAction> actions = super.getRequiredAfterTriggers(game, effectResult, self);

        // Utinni Effect is lost if target not on table or if target is no longer a valid target
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            for (TargetId targetId : self.getBlueprint().getUtinniEffectTargetIds(self.getOwner(), game, self)) {
                PhysicalCard targetCard = self.getTargetedCard(game.getGameState(), targetId);
                if (targetCard != null
                        && !Filters.and(Filters.onTable, getValidUtinniEffectTargetFilterToRemainTargeting(game, self, targetId)).accepts(game, targetCard)) {

                    RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(new UtinniEffectRule(), self);
                    action.setSingletonTrigger(true);
                    action.setText("Make " + GameUtils.getFullName(self) + " lost");
                    // Perform result(s)
                    action.appendEffect(
                            new LoseCardFromTableEffect(action, self));
                    actions.add(action);
                    break;
                }
            }
        }
        return actions;
    }
}
