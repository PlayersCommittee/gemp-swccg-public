package com.gempukku.swccgo.cards.set5.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.InitiateBattleAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: E Chu Ta
 */

public class Card5_138 extends AbstractUsedInterrupt {
    public Card5_138() {
        super(Side.DARK, 3, "E Chu Ta", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("'How rude!'");
        setGameText("One of your protocol droids 'insults' one character present, canceling that character's game text for remainder of turn. OR During your battle phase, initiate a battle where both players have droids (but no presence or spies). Both sides add one battle destiny.");
        addIcons(Icon.CLOUD_CITY);
    }

        @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        String opponent = game.getOpponent(playerId);

        Filter characterPresent = Filters.and(Filters.character, Filters.presentAt(Filters.any));
        Filter yourProtocolDroidWithCharacterPresent = Filters.and(Filters.your(playerId), Filters.protocol_droid, Filters.with(self, characterPresent));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, yourProtocolDroidWithCharacterPresent)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("'Insult' a character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your protocol droid", yourProtocolDroidWithCharacterPresent) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourProtocolDroid) {
                            action.appendTargeting(
                                new TargetCardOnTableEffect(action, playerId, "Choose character to 'insult'", Filters.and(characterPresent, Filters.with(yourProtocolDroid))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard victimCharacter) {
                                            action.addAnimationGroup(yourProtocolDroid, victimCharacter);

                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(yourProtocolDroid) + " and " + GameUtils.getCardLink(victimCharacter),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard yourFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard victimFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId2);
                                                            
                                                            // Send Easter egg
                                                            action.appendCost(new SendMessageEffect(action, GameUtils.getFullName(yourFinalTarget) + ": E chu ta."));
                                                            action.appendCost(new SendMessageEffect(action, GameUtils.getFullName(victimFinalTarget) + ": How rude!"));

                                                            // Perform result(s)
                                                            action.appendEffect(new CancelGameTextUntilEndOfTurnEffect(action, victimFinalTarget));
                                                        }
                                                    }
                                            );


                                        }
                                }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        
         // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.BATTLE)) {

            Filter yourDroids = Filters.and(Filters.your(self), Filters.droid);
            Filter opponentDroids = Filters.and(Filters.opponents(self), Filters.droid);

            Filter locationsWithYourDroid = Filters.sameLocationAs(self, yourDroids);
            Filter locationsWithOpponentDroid = Filters.sameLocationAs(self, opponentDroids);
            Filter locationsWithNoPresence = Filters.unoccupied;
            Filter locationsWithNoSpies = Filters.not(Filters.sameLocationAs(self, Filters.spy));

            Filter locationFilter = Filters.and(locationsWithYourDroid, locationsWithOpponentDroid, locationsWithNoPresence, locationsWithNoSpies);

            List<PhysicalCard> potentialBattleLocations = new LinkedList<>();
            for(PhysicalCard location:Filters.filterTopLocationsOnTable(game, locationFilter)) {
                if (GameConditions.canInitiateBattleAtLocation(playerId, game, location, false, true, true)
                        || GameConditions.canInitiateBattleAtLocation(playerId, game, location, true, true, true)) {
                    potentialBattleLocations.add(location);
                }
            }

            // Check more condition(s)
            if (!potentialBattleLocations.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate battle");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a location", Filters.in(potentialBattleLocations)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses("Initiate battle at " + GameUtils.getCardLink(targetedCard), new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(
                                        new StackActionEffect(action,
                                                new InitiateBattleAction(playerId, finalTarget, false) {
                                                    @Override
                                                    public List<Modifier> getAddedModifiers() {
                                                        List<Modifier> modifiers = new LinkedList<>();
                                                        Filter droidsAtBattleLocation = Filters.and(Filters.droid, Filters.at(finalTarget));

                                                        modifiers.add(new MayInitiateBattleModifier(self, droidsAtBattleLocation));
                                                        modifiers.add(new MayBeBattledModifier(self, droidsAtBattleLocation));

                                                        modifiers.add(new AddsBattleDestinyModifier(self, 1, playerId, true));
                                                        modifiers.add(new AddsBattleDestinyModifier(self, 1, opponent, true));

                                                        return modifiers;
                                                    }
                                                }));
                            }
                        });
                    }
                });
                actions.add(action);
            }
        }
        return actions;
    }

}
