package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.rules.UtinniEffectRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Plastoid Armor
 */
public class Card1_059 extends AbstractEffect {
    //this is a Utinni Effect, but Elom changes it into a normal Effect so having this be an AbstractEffect that will use Utinni Effect methods seems like the best approach
    public Card1_059() {
        super(Side.LIGHT, 5f, PlayCardZoneOption.ATTACHED, Title.Plastoid_Armor, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U2);
        setLore("Luke and Han took the armor suits from fallen stormtroopers and used them as both protection and disguise to penetrate the detention cell block aboard the Death Star.");
        setGameText("Deploy on a Death Star site where a stormtrooper was just lost. Target one of your characters not on Death Star. When target reaches Utinni Effect, relocate to target. Target is now 'disguised:' gains spy skill, power and forfeit +2, and armor = 5.");
        setCardSubtype(CardSubtype.UTINNI);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (!GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return Filters.site;

        return Filters.and(Filters.or(Filters.Rebel, Filters.alien), Filters.at(Filters.mobile_site), Filters.with(self, Filters.title("Elom")));
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return false;

        return super.canPlayCardDuringCurrentPhase(playerId, game, self);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return null;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.stormtrooper, Filters.Death_Star_site)) {
            PhysicalCard location = ((LostFromTableResult) effectResult).getFromLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(location), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return Filters.none;

        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.on(Title.Death_Star)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && target != null
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Relocate to " + GameUtils.getCardLink(target));
            action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(target));
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, target));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter targetAttachedTo = Filters.and(Filters.hasAttached(self), Filters.or(Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1),
                Filters.and(Filters.character, Filters.hasAttached(Filters.and(self, Filters.Effect)))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, targetAttachedTo, Keyword.SPY));
        modifiers.add(new PowerModifier(self, targetAttachedTo, 2));
        modifiers.add(new ForfeitModifier(self, targetAttachedTo, 2));
        modifiers.add(new ResetArmorModifier(self, targetAttachedTo, 5));
        return modifiers;
    }


    //from AbstractUtinniEffect
    @Override
    protected final Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        Filter filter = super.getValidDeployTargetFilterForCardType(playerId, game, self, isSimDeployAttached, ignorePresenceOrForceIcons, deploymentRestrictionsOption, deployAsCaptiveOption);
        if (!isDagobahAllowed()) {
            filter = Filters.and(filter, Filters.not(Filters.locationAndCardsAtLocation(Filters.Dagobah_location)));
        }
        return filter;
    }

    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return Collections.emptyList();
        return Collections.singletonList(TargetId.UTINNI_EFFECT_TARGET_1);
    }


    @Override
    public final Filter getValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return Filters.none;

        Filter filter = Filters.and(Filters.canBeTargetedBy(self), getGameTextValidUtinniEffectTargetFilter(playerId, game, self, deployTarget, targetId));
        if (!isDagobahAllowed()) {
            filter = Filters.and(filter, Filters.not(Filters.locationAndCardsAtLocation(Filters.Dagobah_location)));
        }
        return filter;
    }

    @Override
    public final Filter getValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.and(getGameTextValidUtinniEffectTargetFilterToRemainTargeting(game, self, targetId), Filters.canBeTargetedBy(self));
    }

    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(final Action action, String playerId, SwccgGame game, final PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return super.getTargetCardsWhenDeployedEffects(action, playerId, game, self, target, playCardOption);

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

    protected final List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }

    protected boolean isDagobahAllowed() {
        return false;
    }

    @Override
    public List<Modifier> getWhileInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getWhileInPlayModifiers(game, self);
        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return modifiers;

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

        if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.PLASTOID_ARMOR__CHANGE_DEPLOYMENT))
            return actions;

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