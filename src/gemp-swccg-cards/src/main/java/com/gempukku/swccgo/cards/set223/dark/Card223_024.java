package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: The Emperor's Hand
 */
public class Card223_024 extends AbstractUsedOrLostInterrupt {
    public Card223_024() {
        super(Side.DARK, 4, "The Emperor's Hand", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setGameText("USED: Take Mara Jade, The Emperor's Hand into hand from Reserve Deck; reshuffle. OR If a battle just ended that your [Premium] Mara won, opponent loses 1 Force and, if Emperor on table, may relocate your Mara to a battleground site. LOST: Retrieve Mara Jade's Lightsaber.");
        addIcons(Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPERORS_HAND__UPLOAD_MARA;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Mara into hand from Reserve Deck");

            // Allow response(s)
            action.allowResponses("Take Mara Jade, The Emperor's Hand into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Mara_Jade_The_Emperors_Hand), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_EMPERORS_HAND__RETRIEVE_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Retrieve Mara Jade's Lightsaber");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, Filters.persona(Persona.MARA_JADES_LIGHTSABER)));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.battleEnded(game, effectResult)) {
            BattleState battleState = ((BattleEndedResult)effectResult).getBattleState();

            if (battleState.isWinner(playerId)) {
                Collection<PhysicalCard> yourParticipants = battleState.getCardsParticipatingWhenResultDetermined(playerId);

                if (!Filters.filter(yourParticipants, game, Filters.and(Filters.Mara_Jade, Icon.PREMIUM)).isEmpty()) {
                    final String opponent = game.getOpponent(playerId);

                    // Action 1: force loss but no relocation
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                    action.setText("Cause Force loss");
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new LoseForceEffect(action, opponent, 1));
                                }
                            }
                    );
                    actions.add(action);


                    // Action 2: force loss and relocation
                    Filter maraFilter = Filters.and(Filters.your(self), Filters.Mara_Jade, Filters.canBeRelocatedToLocation(Filters.battleground_site, true, 0));

                    if (GameConditions.canTarget(game, self, Filters.Emperor)
                            && GameConditions.canTarget(game, self, maraFilter)) {

                        final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.USED);
                        action2.setText("Cause Force loss and relocate Mara");


                        action2.appendTargeting(new TargetCardOnTableEffect(action2, playerId, "Choose Mara to relocate", maraFilter) {

                            @Override
                            protected void cardTargeted(int maraTargetGroupId, PhysicalCard targetedCard) {
                                action2.appendTargeting(new TargetCardOnTableEffect(action2, playerId, "Choose battleground site to relocate " + GameUtils.getCardLink(targetedCard) + " to",
                                        Filters.and(Filters.battleground_site, Filters.locationCanBeRelocatedTo(targetedCard, true, 0))) {
                                    @Override
                                    protected void cardTargeted(int siteTargetGroupId, PhysicalCard targetedCard) {
                                        // Allow response(s)
                                        action2.allowResponses(
                                                new RespondablePlayCardEffect(action2) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        final PhysicalCard finalMara = action2.getPrimaryTargetCard(maraTargetGroupId);
                                                        final PhysicalCard finalSite = action2.getPrimaryTargetCard(siteTargetGroupId);
                                                        // Perform result(s)
                                                        action2.appendEffect(
                                                                new LoseForceEffect(action2, opponent, 1));
                                                        action2.appendEffect(
                                                                new RelocateBetweenLocationsEffect(action2, finalMara, finalSite));
                                                    }
                                                }
                                        );
                                    }
                                });
                            }
                        });

                        actions.add(action2);
                    }
                }
            }
        }

        return actions;
    }
}
