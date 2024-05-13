package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LandedResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: It's An Older Code
 */
public class Card8_148 extends AbstractUsedOrLostInterrupt {
    public Card8_148() {
        super(Side.DARK, 3, Title.Its_An_Older_Code, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Even though Vader allowed the stolen shuttle Tydirium to land on Endor, he had a plan to deal with the Rebels.");
        setGameText("USED: If opponent just landed a starship at an exterior site, you may deploy up to three related sites from your Reserve Deck; reshuffle. LOST: Use 2 Force to 'scan' (reveal) opponent's hand. You may place one starship you find there in opponent's Used Pile.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.ITS_AN_OLDER_CODE__DOWNLOAD_RELATED_SITES;

        // Check condition(s)
        if (TriggerConditions.justLandedAt(game, effectResult, opponent, Filters.starship, Filters.exterior_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {
            final PhysicalCard location = ((LandedResult) effectResult).getMovedTo();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Deploy related sites from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to three sites related to " + GameUtils.getCardLink(location) + " from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.site, Filters.relatedLocationEvenWhenNotInPlay(location)), Filters.relatedSite(location), 1, 3, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("'Scan' opponent's hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Reveal opponent's hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                            final Filter starshipFilter = Filters.and(Filters.starship, Filters.canBeTargetedBy(self));
                                            if (GameConditions.hasInHand(game, opponent, starshipFilter)) {
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new YesNoDecision("Do you want to place a starship from opponent's hand in Used Pile?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        game.getGameState().sendMessage(playerId + " chooses to place a starship in Used Pile");
                                                                        action.appendEffect(
                                                                                new PutCardFromHandOnUsedPileEffect(action, playerId, opponent, starshipFilter, false) {
                                                                                    @Override
                                                                                    public String getChoiceText(int numCardsToChoose) {
                                                                                        return "Choose starship to place in Used Pile";
                                                                                    }
                                                                                }
                                                                        );
                                                                    }
                                                                    @Override
                                                                    protected void no() {
                                                                        game.getGameState().sendMessage(playerId + " chooses to not place a starship in Used Pile");
                                                                    }
                                                                }
                                                        )
                                                );
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}