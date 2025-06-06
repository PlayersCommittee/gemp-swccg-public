package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfForcePileAndReserveDeckAndUsedPileAndReturnOneCardToEachEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.InitiateBattleAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.*;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Captive Fury
 */
public class Card5_036 extends AbstractUsedOrLostInterrupt {
    public Card5_036() {
        super(Side.LIGHT, 4, Title.Captive_Fury, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("Chewie's life debt to Han forced him to act, retaliating unexpectedly against his captors.");
        setGameText("USED: Cancel Force drain bonus from IT-O this turn.  LOST: During your battle phase, any of your escorted captives at same site may initiate and participate in one battle (they may not use weapons or devices and you may not voluntarily forfeit or relocate them).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, final PhysicalCard self) {

        Filter itoFilter = Filters.and(Filters.opponents(self), Filters.title(Title.IT0));
        if (GameConditions.canTarget(game, self, itoFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel IT-O's captive Force drain bonus this turn");

            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose IT-O", itoFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s captive Force drain bonus this turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new CancelForceDrainBonusesFromCardModifier(self, Filters.sameCardId(finalTarget)),
                                                            "Cancels " + GameUtils.getCardLink(targetedCard) + "'s captive Force drain bonus this turn"
                                                    )
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );

            return Collections.singletonList(action);
        }

        if (GameConditions.isDuringYourPhase(game, self, Phase.BATTLE)) {
            final var yourEscortedCaptive = Filters.and(Filters.escortedCaptive, Filters.your(playerId), Filters.not(Filters.frozenCaptive));
            var validCaptives = getBattleEligibleEscortedCaptives(game, self, yourEscortedCaptive);
            if(validCaptives == null || validCaptives.isEmpty())
                return null;


            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Initiate battle with escorted captives");
            action.setActionMsg("Initiate battle with escorted captives");

            var selectedCaptives = new ArrayList<PhysicalCard>();

            chooseCaptives(action, playerId, validCaptives, selectedCaptives);

            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Update usage limit(s)
                            action.addAnimationGroup(selectedCaptives);
                            var firstCaptive = selectedCaptives.getFirst();
                            var escort = firstCaptive.getEscort();
                            var location = escort.getCardAttachedToAtLocation().getAtLocation();
                            if(location == null && (escort.isPassengerOf() || escort.isPilotOf())) {
                                location = escort.getAttachedTo().getAtLocation();
                            }

                            action.setActionMsg("Have " + GameUtils.getCardLink(firstCaptive) + " initiate a battle");
                            action.appendEffect(
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            for(var captive : selectedCaptives) {
                                                //Moving to a location causes the escort to be cleared, so we will
                                                // restore the escort status; our furious captive is not *escaping*,
                                                // just moonlighting on the other side of the location for a battle.
                                                var escort = captive.getEscort();
                                                //According to the AR entry, this is supposed to go on the LS side, but
                                                // if you leave the defaults here then the captive ends up on the DS side,
                                                // but separated from the others. It's actually a little more clear.
                                                game.getGameState().moveCardToLocation(captive, escort.getCardAttachedToAtLocation().getAtLocation());
                                                captive.setCaptiveEscort(escort);
                                            }
                                        }
                                    }
                            );

                            initiateBattleWithCaptives(game, playerId, self, action, location, selectedCaptives);

                            releaseOrReattachCaptives(game, action, selectedCaptives);
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<PhysicalCard> getBattleEligibleEscortedCaptives(SwccgGame game, PhysicalCard self, Filter filter) {
        var captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, filter);

        return captives.stream().filter(card -> {
            var location = card.getCardAttachedToAtLocation().getAtLocation();
            var player = card.getOwner();
			return !GameConditions.hasParticipatedInBattleThisTurn(game, card) &&
                    (
                        GameConditions.canInitiateBattleAtLocation(player, game, location, false, true, true)
                        || GameConditions.canInitiateBattleAtLocation(player, game, location, true, true, true)
                    );
		}).toList();
    }

    private static void chooseCaptives(PlayInterruptAction action, String playerId, List<PhysicalCard> initialTargets, List<PhysicalCard> selectedCaptives) {

        action.appendTargeting(
                new TargetCardsAtSameLocationEffect(action, playerId, "Choose escorted captives to battle", 1,
                        Integer.MAX_VALUE, SpotOverride.INCLUDE_CAPTIVE, Filters.in(initialTargets)) {
                    @Override
                    protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> targetedCards) {
                        selectedCaptives.addAll(targetedCards);
                    }
                });
    }


    private static void initiateBattleWithCaptives(final SwccgGame game, final String playerId, final PhysicalCard self,
            final PlayInterruptAction action, final PhysicalCard location, final List<PhysicalCard> chosenCaptives) {

        var battleAction = new InitiateBattleAction(playerId, location, false) {
            @Override
            public List<Modifier> getAddedModifiers() {
                List<Modifier> modifiers = new LinkedList<>();

                modifiers.add(new CaptiveMayParticipateInBattleModifier(self, Filters.in(chosenCaptives)));
                modifiers.add(new MayNotUseWeaponsModifier(self, Filters.in(chosenCaptives)));
                modifiers.add(new MayNotUseDevicesModifier(self, Filters.in(chosenCaptives)));
                modifiers.add(new MayNotBeForfeitedInBattleModifier(self, Filters.and(Filters.in(chosenCaptives),Filters.not(Filters.hit))));
                modifiers.add(new MayNotMoveAwayFromLocationModifier(self, Filters.in(chosenCaptives), Filters.samePermanentCardId(location)));

                return modifiers;
            }
        };

        action.appendEffect(new StackActionEffect(action, battleAction));
    }

    private static void releaseOrReattachCaptives(SwccgGame game, PlayInterruptAction action, List<PhysicalCard> selectedCaptives) {
        var gameState = game.getGameState();
        var effect = new UnrespondableEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                for(var captive : selectedCaptives) {
                    if(!captive.isCaptive() || captive.getZone() != Zone.AT_LOCATION)
                        continue;

                    //We now move everyone still at the site back to their escort, unless that escort was lost during the fight
                    var escort = captive.getEscort();

                    if(escort.getZone().isInPlay()) {

                        boolean reattach = true;

                        //If the escort is in a vehicle that is at capacity, they must disembark before escorting
                        var attachedTo = escort.getAttachedTo();
                        if (attachedTo != null && (escort.isPilotOf() || escort.isPassengerOf())
                                && (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP || attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE)) {
                            if (Filters.or(Filters.piloting(attachedTo), Filters.aboardAsPassenger(attachedTo)).accepts(gameState, game.getModifiersQuerying(), escort)
                                    && gameState.getAvailablePassengerCapacity(game.getModifiersQuerying(), attachedTo, captive) < 1) {
                                action.appendEffect(
                                        new DisembarkEffect(action, escort,game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), escort), false,false));
                                //We will be reattaching after the disembark, so don't handle it outside this function
                                reattach = false;
                                action.appendEffect(new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        game.getGameState().seizeCharacter(game, captive, escort);
                                        captive.setCaptiveEscort(escort);
                                    }
                                });
                            }
                        }

                        //If the above vehicle edge case was not a problem
                        if (reattach) {
                            game.getGameState().seizeCharacter(game, captive, escort);
                            captive.setCaptiveEscort(escort);
                        }
                    }
                    else {
                        //Escort was lost, missing, etc, so we release the captive
                        action.appendEffect(new ReleaseCaptiveEffect(action, captive));
                    }
                }
            }
        };
        action.appendEffect(effect);
    }
}