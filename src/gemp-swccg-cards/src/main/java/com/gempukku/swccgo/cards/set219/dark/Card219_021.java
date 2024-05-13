package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ArtworkCardRevealedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Thrawn Pincer
 */
public class Card219_021 extends AbstractUsedOrLostInterrupt {
    public Card219_021() {
        super(Side.DARK, 6, "Thrawn Pincer", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setGameText("USED: Add 1 to hyperspeed of a dreadnaught or Star Destroyer for each artwork card on table for remainder of turn. " +
                    "LOST: Once per game, if your [Set 19] objective just ‘studied’ a vehicle or starship, relocate your Star Destroyer from anywhere on table to that battle.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter dreadnaughtOrStarDestroyer = Filters.or(Filters.Dreadnaught, Filters.Star_Destroyer);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Thrawns_Art_Collection, Filters.hasStacked(Filters.any)))
                && GameConditions.canTarget(game, self, dreadnaughtOrStarDestroyer)) {

            final int numArtworkOnTable = Filters.filterStacked(game, Filters.stackedOn(Filters.findFirstActive(game, self, Filters.Thrawns_Art_Collection))).size();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Add " + numArtworkOnTable + " to hyperspeed");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship to add hyperspeed to", dreadnaughtOrStarDestroyer) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new HyperspeedModifier(self, finalTarget, numArtworkOnTable),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " hyperspeed +" + numArtworkOnTable));

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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.THRAWN_PINCER__RELOCATE_STAR_DESTROYER;

        if (effectResult.getType() == EffectResult.Type.ARTWORK_CARD_REVEALED
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Star_Destroyer, Filters.canBeTargetedBy(self), Filters.canBeRelocatedToLocation(Filters.battleLocation, true, 0)))) {

            PhysicalCard artwork = ((ArtworkCardRevealedResult) effectResult).getCard();

            if (artwork != null
                    && Filters.starship.accepts(game, artwork)) {

                final PhysicalCard battleLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.battleLocation);
                if (battleLocation != null) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
                    action.setText("Relocate Star Destroyer");

                    action.appendUsage(
                            new OncePerGameEffect(action));
                    action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a Star Destroyer to relocate to " + GameUtils.getCardLink(battleLocation), Filters.and(Filters.your(self), Filters.Star_Destroyer, Filters.canBeRelocatedToLocation(battleLocation, true, 0))) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.allowResponses("Relocate "+GameUtils.getCardLink(targetedCard)+" to "+GameUtils.getCardLink(battleLocation), new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard starDestroyer = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(new RelocateBetweenLocationsEffect(action, starDestroyer, battleLocation));
                                }
                            });
                        }
                    });

                    return Collections.singletonList(action);
                }
            }
        }

        return null;
    }
}
