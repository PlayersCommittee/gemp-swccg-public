package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Oo-ta Goo-ta, Solo? (V)
 */
public class Card210_041 extends AbstractUsedOrLostInterrupt {
    public Card210_041() {
        super(Side.DARK, 5, Title.Oota_Goota_Solo, Uniqueness.UNRESTRICTED, ExpansionSet.SET_10, Rarity.V);
        setLore("Greedo cheskopokuta klees ruya Solo. Hoko yanee boopa gush Cantina. Cheeco wa Solo's anye nyuma Greedo vakee. Jabba kul steeka et en anpaw.");
        setGameText("USED: Cancel Nabrun Leids. [Immune to Sense.] OR Use 1 Force to take a Rodian (or a non-unique alien of your Rep's species) into hand from Reserve Deck; reshuffle. LOST: During your move phase, \"break cover\" of an Undercover spy.");
        addIcons(Icon.A_NEW_HOPE);
        setVirtualSuffix(true);
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Cancel Nabrun Leids. [Immune to Sense.]

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Nabrun_Leids)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setImmuneTo(Title.Sense);

            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // USED: Cancel Nabrun Leids. [Immune to Sense.]
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Nabrun_Leids)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Nabrun_Leids, Title.Nabrun_Leids);
            actions.add(action);
        }


        // USED: Use 1 Force to take a Rodian (or a non-unique alien of your Rep's species) into hand from Reserve Deck; reshuffle.
        GameTextActionId gameTextActionId = GameTextActionId.OOTA_GOOTA__UPLOAD_RODIAN_OR_REP_SPECIES_ALIEN;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            Filter uploadCharacterTargets;
            PhysicalCard rep = game.getGameState().getRep(playerId);
            if (rep != null) {
                // Player is using a Rep
                Species repSpecies = rep.getBlueprint().getSpecies();
                Filter nonUniqueAlien = Filters.and(Filters.non_unique, Filters.alien);
                uploadCharacterTargets = Filters.or(Filters.Rodian, Filters.and(nonUniqueAlien, Filters.species(repSpecies)));
            } else {
                // Player is NOT using a rep
                uploadCharacterTargets = Filters.Rodian;
            }

            final Filter uploadableCharacters = uploadCharacterTargets;
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take card into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a Rodian or non-unique alien of your Rep's species into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, uploadableCharacters, true));
                        }
                    }
            );
            actions.add(action);
        }

        // LOST: During your move phase, "break cover" of an Undercover spy.

        // NOTE: We have a pending question out to D&D as to whether this is ANY Undercover spy, or just opponents
        //       Leaving this line in in case we need to swap it out
        // Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy);
        Filter targetFilter = Filters.undercover_spy;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE) &&
            GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Break a spy's cover");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);

                            // Allow response(s)
                            action.allowResponses("'Break cover' of " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSpy = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new BreakCoverEffect(action, finalSpy));
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