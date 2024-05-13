package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedToConcealedOnlyCondition;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachAndConcealEffect;
import com.gempukku.swccgo.logic.effects.DetachAndUnconcealEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeUsedByLandedStarshipModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Landing Claw
 */
public class Card4_011 extends AbstractDevice {
    public Card4_011() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, Title.Landing_Claw, Uniqueness.RESTRICTED_2, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Special modification for starfighters. Size may vary depending on mass of starship. Allows a stable connection to a spacedock, uneven terrain or even another starship.");
        setGameText("Deploy on one of your starfighters. During any control phase, may target one opponent's capital starship present. Starfighter attaches to target (automatically moves with target and is 'concealed'). May detach at any time (even as target begins to move).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeUsedByLandedStarshipModifier(self));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.starfighter);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.starfighter;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.here(self), new AttachedToConcealedOnlyCondition(self), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard attachedTo = self.getAttachedTo();

        // Check condition(s)
        if (attachedTo != null && !attachedTo.isConcealed()
                && GameConditions.canAttach(game, attachedTo)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            final Filter filter = Filters.and(Filters.opponents(self), Filters.capital_starship, Filters.present(self));
            if (GameConditions.canTarget(game, self, filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Attach to opponent's starship");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                action.appendUsage(
                        new UseDeviceEffect(action, self));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose capital starship", filter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Attach " + GameUtils.getCardLink(attachedTo) + " to " + GameUtils.getCardLink(targetedCard),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AttachAndConcealEffect(action, attachedTo, targetedCard));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard attachedTo = self.getAttachedTo();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (attachedTo != null && GameConditions.isOnlyConcealed(game, self) && attachedTo.isConcealed() && attachedTo.getAttachedTo() != null
                && GameConditions.canUseDevice(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Detach from opponent's starship");
            action.setActionMsg("Detach " + GameUtils.getCardLink(attachedTo) + " from " + GameUtils.getCardLink(attachedTo.getAttachedTo()));
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DetachAndUnconcealEffect(action, attachedTo));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final PhysicalCard attachedTo = self.getAttachedTo();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (attachedTo != null && GameConditions.isOnlyConcealed(game, self) && attachedTo.isConcealed() && attachedTo.getAttachedTo() != null
                && TriggerConditions.moving(game, effectResult, attachedTo.getAttachedTo())
                && GameConditions.canUseDevice(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Detach from opponent's starship");
            action.setActionMsg("Detach " + GameUtils.getCardLink(attachedTo) + " from " + GameUtils.getCardLink(attachedTo.getAttachedTo()));
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DetachAndUnconcealEffect(action, attachedTo));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard lostFromAttachedTo = ((LostFromTableResult) effectResult).getFromAttachedTo();
            if (lostFromAttachedTo != null && lostFromAttachedTo.isConcealed() && lostFromAttachedTo.getAttachedTo() != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Detach " + GameUtils.getFullName(lostFromAttachedTo) + " from opponent's starship");
                action.setActionMsg("Detach " + GameUtils.getCardLink(lostFromAttachedTo) + " from " + GameUtils.getCardLink(lostFromAttachedTo.getAttachedTo()));
                // Perform result(s)
                action.appendEffect(
                        new DetachAndUnconcealEffect(action, lostFromAttachedTo));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}