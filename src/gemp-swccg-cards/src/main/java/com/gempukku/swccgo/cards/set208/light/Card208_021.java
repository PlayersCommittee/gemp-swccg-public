package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnForCardTitleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CostToDrawDestinyCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used
 * Title: Suppressive Fire (V)
 */
public class Card208_021 extends AbstractUsedInterrupt {
    public Card208_021() {
        super(Side.LIGHT, 3, "Suppressive Fire", Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Echo Base heavy weapons units provided covering fire, allowing Alliance personnel to escape Hoth.");
        setGameText("Once per turn, if drawn for destiny, you may activate 1 Force. During battle at a site, choose: Your total power is +1 for each of your characters present. OR If you have a weapon present, opponent must first use or lose 1 Force to draw their first battle destiny.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SUPPRESSIVE_FIRE__ACTIVATE_FORCE;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurnForCardTitle(game, self, gameTextActionId)
                && GameConditions.canActivateForce(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnForCardTitleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);
        final int gameTextSourceCardId = self.getCardId();

        Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.presentInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)) {
            Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(characterFilter, Filters.canBeTargetedBy(self)));
            final int amountToAdd = characters.size();
            if (amountToAdd > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + amountToAdd + " to total power");
                action.addSecondaryTargetFilter(Filters.and(Filters.site, Filters.battleLocation));
                action.addSecondaryTargetFilter(characterFilter);
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, amountToAdd, playerId,
                                                "Adds " + amountToAdd + " to total power"));
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter weaponFilter = Filters.and(Filters.your(self), Filters.weapon_or_character_with_permanent_weapon, Filters.presentInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canTarget(game, self, weaponFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target weapon present");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose weapon", weaponFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedWeapon) {
                            action.addAnimationGroup(targetedWeapon);
                            action.addSecondaryTargetFilter(Filters.and(Filters.site, Filters.battleLocation));
                            // Allow response(s)
                            action.allowResponses("Make opponent must first use or lose 1 Force to draw their first battle destiny by targeting " + GameUtils.getCardLink(targetedWeapon),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            final int permCardId = self.getPermanentCardId();
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                    List<TriggerAction> actions2 = new LinkedList<TriggerAction>();
                                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                                    // Check condition(s)
                                                                    if (TriggerConditions.isCheckingCostsToDrawBattleDestiny(game, effectResult, opponent)) {
                                                                        final CostToDrawDestinyCardResult costToDrawDestinyCardResult = (CostToDrawDestinyCardResult) effectResult;
                                                                        if (costToDrawDestinyCardResult.getNumDestinyDrawnSoFar() == 0) {

                                                                            final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                                            action2.setText("Add cost to draw battle destiny");
                                                                            action2.setActionMsg("Make opponent use or lose 1 Force to draw a card for battle destiny");
                                                                            // Perform result(s)
                                                                            action2.appendEffect(
                                                                                    new PassthruEffect(action2) {
                                                                                        @Override
                                                                                        protected void doPlayEffect(final SwccgGame game) {
                                                                                            final GameState gameState = game.getGameState();
                                                                                            if (GameConditions.canUseForce(game, opponent, 1)) {
                                                                                                // Ask player to Use 1 Force, Lose 1 Force, or neither
                                                                                                action2.appendEffect(
                                                                                                        new PlayoutDecisionEffect(action2, opponent,
                                                                                                                new MultipleChoiceAwaitingDecision("Choose effect to draw card for battle destiny", new String[]{"Use 1 Force", "Lose 1 Force", "Neither"}) {
                                                                                                                    @Override
                                                                                                                    protected void validDecisionMade(int index, String result) {
                                                                                                                        if (index == 0) {
                                                                                                                            gameState.sendMessage(opponent + " chooses to use 1 Force");
                                                                                                                            action2.appendEffect(
                                                                                                                                    new UseForceEffect(action2, opponent, 1));
                                                                                                                        } else if (index == 1) {
                                                                                                                            gameState.sendMessage(opponent + " chooses to lose 1 Force");
                                                                                                                            action2.appendEffect(
                                                                                                                                    new LoseForceEffect(action2, opponent, 1, true));
                                                                                                                        } else {
                                                                                                                            gameState.sendMessage(opponent + " chooses to neither use or lose 1 Force to draw a card for battle destiny");
                                                                                                                            costToDrawDestinyCardResult.costToDrawCardFailed(true);
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                        )
                                                                                                );
                                                                                            } else {
                                                                                                action2.appendEffect(
                                                                                                        new PlayoutDecisionEffect(action2, opponent,
                                                                                                                new YesNoDecision("Do you want to lose 1 Force to draw a card for battle destiny?") {
                                                                                                                    @Override
                                                                                                                    protected void yes() {
                                                                                                                        action2.appendEffect(
                                                                                                                                new LoseForceEffect(action2, opponent, 1, true));
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    protected void no() {
                                                                                                                        gameState.sendMessage(opponent + " chooses to not lose 1 Force to draw a card for battle destiny");
                                                                                                                        costToDrawDestinyCardResult.costToDrawCardFailed(true);
                                                                                                                    }
                                                                                                                }
                                                                                                        )
                                                                                                );
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                            actions2.add(action2);
                                                                        }
                                                                    }
                                                                    return actions2;
                                                                }
                                                            }
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}