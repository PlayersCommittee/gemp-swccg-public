package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.BattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Local Trouble
 */
public class Card1_256 extends AbstractLostInterrupt {
    public Card1_256() {
        super(Side.DARK, 4, Title.Local_Trouble, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("'Look like somebody's beginning to take an interest in your handiwork.' Imperial stormtroopers coerce local residents to assist them in the apprehension of Rebel scum.");
        setGameText("Use 1 Force at the beginning of your battle phase to allow any two of your Stormtroopers in the Cantina to battle any one opponent's character (your choice). You may add one battle destiny. No other battles may occur in Cantina this turn.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // "At the beginning of your battle phase" is a start-of-phase trigger only.
        // After that window is skipped, Local Trouble may not be played as a top-level action.
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.BATTLE, playerId)
                && !GameConditions.isDuringBattle(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpotLocation(game, Filters.Cantina)) {

            PlayInterruptAction action = createLocalTroubleAction(playerId, game, self);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction createLocalTroubleAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final PhysicalCard cantina = Filters.findFirstFromTopLocationsOnTable(game, Filters.Cantina);
        if (cantina == null) {
            return null;
        }

        final Filter stormtrooperFilter = Filters.and(Filters.your(self), Filters.stormtrooper, Filters.at(cantina),
                Filters.canParticipateInBattleAt(cantina, playerId), Filters.canBeTargetedBy(self));
        // Forum 79090: cannot battle a character without presence (e.g. a droid)
        final Filter opponentFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(cantina),
                Filters.abilityMoreThan(0), Filters.canParticipateInBattleAt(cantina, playerId), Filters.canBeTargetedBy(self));

        if (!GameConditions.canSpot(game, self, 2, stormtrooperFilter)
                || !GameConditions.canTarget(game, self, opponentFilter)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Initiate Local Trouble battle");
        // Choose target(s) before paying so Sense/Derlin can respond
        action.appendTargeting(
                new TargetCardsOnTableEffect(action, playerId, "Choose two Stormtroopers", 2, 2, stormtrooperFilter) {
                    @Override
                    protected void cardsTargeted(final int stormtrooperTargetGroupId, Collection<PhysicalCard> targetedStormtroopers) {
                        action.addAnimationGroup(targetedStormtroopers);
                        action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentFilter) {
                                    @Override
                                    protected void cardTargeted(final int opponentTargetGroupId, PhysicalCard targetedOpponent) {
                                        action.addAnimationGroup(targetedOpponent);
                                        // Pay cost(s)
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, 1));
                                        // Allow response(s)
                                        action.allowResponses("Allow " + GameUtils.getAppendedNames(targetedStormtroopers) + " to battle " + GameUtils.getCardLink(targetedOpponent),
                                                new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        Collection<PhysicalCard> finalStormtroopers = action.getPrimaryTargetCards(stormtrooperTargetGroupId);
                                                        PhysicalCard finalOpponent = action.getPrimaryTargetCard(opponentTargetGroupId);
                                                        PhysicalCard battleLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.Cantina);
                                                        if (finalStormtroopers == null || finalStormtroopers.size() != 2 || finalOpponent == null || battleLocation == null) {
                                                            return;
                                                        }

                                                        Collection<PhysicalCard> participants = new LinkedList<PhysicalCard>();
                                                        participants.addAll(finalStormtroopers);
                                                        participants.add(finalOpponent);

                                                        final PhysicalCard localTroubleSource = self;
                                                        final int gameTextSourceCardId = self.getCardId();

                                                        // Apply even if the battle is later canceled
                                                        action.appendEffect(
                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                        new MayNotInitiateBattleAtLocationModifier(self, Filters.Cantina),
                                                                        "No other battles may occur in Cantina this turn"));
                                                        // Optional "you may add one battle destiny" popup during the battle
                                                        action.appendEffect(
                                                                new AddUntilEndOfTurnActionProxyEffect(action,
                                                                        new AbstractActionProxy() {
                                                                            @Override
                                                                            public List<TriggerAction> getOptionalAfterTriggers(String triggerPlayerId, SwccgGame game, EffectResult effectResult) {
                                                                                if (!triggerPlayerId.equals(playerId)) {
                                                                                    return null;
                                                                                }
                                                                                if (TriggerConditions.battleInitiated(game, effectResult)
                                                                                        && game.getGameState().isDuringLocalTroubleBattle()
                                                                                        && GameConditions.canAddBattleDestinyDraws(game, localTroubleSource)) {
                                                                                    final OptionalGameTextTriggerAction destinyAction = new OptionalGameTextTriggerAction(localTroubleSource, playerId, gameTextSourceCardId);
                                                                                    destinyAction.setText("Add one battle destiny");
                                                                                    destinyAction.appendEffect(
                                                                                            new AddBattleDestinyEffect(destinyAction, 1, playerId));
                                                                                    return Collections.singletonList((TriggerAction) destinyAction);
                                                                                }
                                                                                return null;
                                                                            }
                                                                        }
                                                                ));
                                                        action.appendEffect(
                                                                new BattleEffect(action, battleLocation, true, participants, Collections.emptyList()));
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}
