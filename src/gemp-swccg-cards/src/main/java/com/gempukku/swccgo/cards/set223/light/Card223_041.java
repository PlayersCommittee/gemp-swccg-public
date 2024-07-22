package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Out Of Commission (V)
 */
public class Card223_041 extends AbstractUsedInterrupt {
    public Card223_041() {
        super(Side.LIGHT, 5, Title.Out_Of_Commission, Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setLore("I hope that old man got that tractor beam out of commission or this is gonna be a real short trip.");
        setGameText("Choose an artwork card to be lost. OR For remainder of turn, forfeit values may not be increased and opponent may not target your 'hit' cards to be lost. OR During your control phase, use 1 Force to relocate [Set 1] Obi-Wan to an adjacent site.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canSpot(game, self, Filters.and(Filters.Thrawns_Art_Collection, Filters.hasStacked(Filters.any)))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Choose an artwork card to be lost.");
            action.setActionMsg("Choose a card stacked on Thrawn's Art Collection in owner's Lost Pile");
            action.appendTargeting(
                new ChooseStackedCardEffect(action, playerId, Filters.Thrawns_Art_Collection) {
                    @Override
                    protected void cardSelected(final PhysicalCard selectedCard) {
                        // Allow response(s)
                        action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));
                                }
                            });
                }
            });
            actions.add(action);
        }

        PlayInterruptAction action1 = new PlayInterruptAction(game, self);
        action1.setText("Modify forfeit values and prevent 'hit' cards from being lost");
        action1.setActionMsg("Prevent forfeit values from being increased and opponent from targeting your 'hit' cards to be lost.");
        action1.allowResponses(
            new RespondablePlayCardEffect(action1) {
                @Override
                protected void performActionResults(Action action1) {
                    action1.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action1, 
                            new MayNotHaveForfeitValueIncreasedModifier(self, Filters.any), playerId));
                    action1.appendEffect(
                        new AddUntilEndOfTurnModifierEffect(action1, 
                        new MayNotTargetToBeLostModifier(self, Filters.and(Filters.your(self), Filters.hit, Filters.any)), playerId));
                }
            }
        );
        actions.add(action1);


        Filter setOneObi = Filters.and(Filters.icon(Icon.VIRTUAL_SET_1), Filters.ObiWan);

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, setOneObi)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpotLocation(game, Filters.adjacentSiteTo(self, setOneObi))) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate [Set 1] Obi-Wan to adjacent site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan to relocate", setOneObi) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard obiWanToRelocate) {
                            Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game,
                                    Filters.and(Filters.adjacentSite(obiWanToRelocate), Filters.locationCanBeRelocatedTo(obiWanToRelocate, 0)));
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(obiWanToRelocate) + " to", Filters.in(otherSites)) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard siteSelected) {
                                            action.addAnimationGroup(obiWanToRelocate);
                                            action.addAnimationGroup(siteSelected);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, obiWanToRelocate, siteSelected, 1));
                                            // Allow response(s)
                                            action.allowResponses("Relocate " + GameUtils.getCardLink(obiWanToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, finalCharacter, siteSelected));
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
        return actions;
    }
}