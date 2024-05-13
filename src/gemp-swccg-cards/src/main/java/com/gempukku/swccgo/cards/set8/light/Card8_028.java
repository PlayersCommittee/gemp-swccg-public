package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Sergeant Bruckman
 */
public class Card8_028 extends AbstractRebel {
    public Card8_028() {
        super(Side.LIGHT, 2, 1, 2, 1, 3, "Sergeant Bruckman", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Corellian Scout who served as point man for General Solo's strike team. Recruited by General Madine for the Alliance commando unit.");
        setGameText("When opponent has just initiated a battle at same exterior site and all your ability here is provided by scouts, may reveal top card of Reserve Deck. If that card is a scout, may deploy it here for free (otherwise, replace on top of Reserve Deck).");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, game.getOpponent(playerId), Filters.and(Filters.exterior_site, Filters.sameSite(self)))
                && GameConditions.isAllAbilityAtLocationProvidedBy(game, self, playerId, Filters.here(self), Filters.scout)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reveal top card of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new RevealTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardRevealed(final PhysicalCard revealedCard) {
                            if (Filters.and(Filters.scout, Filters.deployableToLocation(self, Filters.here(self), true, 0)).accepts(game.getGameState(), game.getModifiersQuerying(), revealedCard)) {
                                // Ask player to deploy scout for free
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(revealedCard) + " for free?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new DeployCardToLocationFromReserveDeckEffect(action, revealedCard, Filters.here(self), true, false, false));
                                                    }
                                                    @Override
                                                    protected void no() {
                                                        game.getGameState().sendMessage(playerId + " chooses to not deploy " + GameUtils.getCardLink(revealedCard));
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
