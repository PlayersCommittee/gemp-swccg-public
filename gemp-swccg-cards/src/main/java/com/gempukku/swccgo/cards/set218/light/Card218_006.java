package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.RevealCardFromOwnHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Take A Seat, Young Skywalker
 */
public class Card218_006 extends AbstractUsedOrLostInterrupt {
    public Card218_006() {
        super(Side.LIGHT, 4, "Take A Seat, Young Skywalker", Uniqueness.RESTRICTED_2);
        setGameText("USED: Reveal a random card from your hand to deploy Jedi Council Chamber from Reserve Deck; reshuffle. LOST: During your move phase, relocate Anakin to Jedi Council Chamber.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.SPEAK_WITH_THE_JEDI_COUNCIL__DOWNLOAD_JEDI_COUNCIL_CHAMBER;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Jedi_Council_Chamber)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))) {

            List<PhysicalCard> hand = new LinkedList<>(Filters.filter(game.getGameState().getHand(playerId), game, Filters.not(self)));

            if (!hand.isEmpty()) {
                Collections.shuffle(hand);
                final PhysicalCard toReveal = hand.get(0);

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy Jedi Council Chamber from Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendCost(
                                        new RevealCardFromOwnHandEffect(action, playerId, toReveal));
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Council_Chamber, true));
                            }
                        }
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // LOST: During your move phase, relocate Anakin to Jedi Council Chamber.
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canSpotLocation(game, Filters.Jedi_Council_Chamber)
                && GameConditions.canTarget(game, self, Filters.and(Filters.Anakin, Filters.canBeRelocatedToLocation(Filters.Jedi_Council_Chamber, true, 0)))) {

            final PhysicalCard jcc = Filters.findFirstFromTopLocationsOnTable(game, Filters.Jedi_Council_Chamber);
            if (jcc != null) {
                final PhysicalCard anakin = Filters.findFirstActive(game, self, Filters.and(Filters.Anakin, Filters.canBeRelocatedToLocation(jcc, true, 0)));

                if (anakin != null) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                    action.setText("Relocate Anakin to Jedi Council Chamber");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Anakin", Filters.and(anakin)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard characterToRelocate) {
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose Jedi Council Chamber to relocate " + GameUtils.getCardLink(characterToRelocate) + " to", Filters.and(jcc)) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, final PhysicalCard siteSelected) {
                                                    action.addAnimationGroup(characterToRelocate);
                                                    action.addAnimationGroup(siteSelected);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, siteSelected, 0));
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(characterToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);
                                                                    PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId2);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacter, finalSite));
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
            }
        }

        return actions;
    }
}