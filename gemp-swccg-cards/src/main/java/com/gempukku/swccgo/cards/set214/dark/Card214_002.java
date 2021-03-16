package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bestoon Legacy
 */
public class Card214_002 extends AbstractStarfighter {
    public Card214_002() {
        super(Side.DARK, 4, 3, 3, null, 4, 5, 4, "Bestoon Legacy", Uniqueness.UNIQUE);
        setLore("");
        setGameText("May add 2 pilots and 3 passengers. During your control phase, may peek at the top card of your Reserve Deck; may place it on Used Pile (if card is an assassin, may take it into hand).");
        addPersona(Persona.BESTOON_LEGACY);
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_14, Icon.INDEPENDENT);
        addModelType(ModelType.WTK_85A_INTERSTELLAR_TRANSPORT);
        addKeyword(Keyword.TRANSPORT_SHIP);
        setMatchingPilotFilter(Filters.Ochi);
        setPilotCapacity(2);
        setPassengerCapacity(3);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top card of your Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardPeekedAt(final PhysicalCard revealedCard) {
                            if (Filters.assassin.accepts(game, revealedCard)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Take " + GameUtils.getCardLink(revealedCard) + " into hand?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, revealedCard, false)
                                                        );
                                                    }

                                                    @Override
                                                    protected void no() {
                                                        action.appendEffect(
                                                                new PlayoutDecisionEffect(action, playerId,
                                                                        new YesNoDecision("Place " + GameUtils.getCardLink(revealedCard) + " in Used Pile?") {
                                                                            @Override
                                                                            protected void yes() {
                                                                                action.appendEffect(
                                                                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, revealedCard, Zone.USED_PILE, true)
                                                                                );
                                                                            }
                                                                        })
                                                        );
                                                    }
                                                })
                                );
                            } else {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Place " + GameUtils.getCardLink(revealedCard) + " in Used Pile?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new PutCardFromReserveDeckOnTopOfCardPileEffect(action, revealedCard, Zone.USED_PILE, true)
                                                        );
                                                    }
                                                })
                                );
                            }
                        }
                    });

            actions.add(action);
        }

        return actions;
    }
}
