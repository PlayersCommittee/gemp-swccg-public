package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.BattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

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
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.BATTLE)
                && !GameConditions.isDuringBattle(game)
                && !GameConditions.hasInitiatedBattleThisTurn(game, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpotLocation(game, Filters.Cantina)) {

            final PhysicalCard cantina = Filters.findFirstFromTopLocationsOnTable(game, Filters.Cantina);
            if (cantina == null) {
                return null;
            }

            final Filter stormtrooperFilter = Filters.and(Filters.your(self), Filters.stormtrooper, Filters.at(cantina),
                    Filters.canParticipateInBattleAt(cantina, playerId), Filters.canBeTargetedBy(self));
            // Forum 79090: cannot battle a character without presence (e.g. a droid)
            final Filter opponentFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(cantina),
                    Filters.abilityMoreThan(0), Filters.canParticipateInBattleAt(cantina, playerId), Filters.canBeTargetedBy(self));

            if (GameConditions.canSpot(game, self, 2, stormtrooperFilter)
                    && GameConditions.canTarget(game, self, opponentFilter)) {

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

                                                                Collection<Modifier> extraModifiers = Collections.<Modifier>singletonList(new AddsBattleDestinyModifier(self, 1));

                                                                // Apply even if the battle is later canceled
                                                                action.appendEffect(
                                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                                new MayNotInitiateBattleAtLocationModifier(self, Filters.Cantina),
                                                                                "No other battles may occur in Cantina this turn"));
                                                                action.appendEffect(
                                                                        new BattleEffect(action, battleLocation, true, participants, extraModifiers));
                                                            }
                                                        }
                                                );
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
}
