package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Starship
 * Subtype: Starfighter
 * Title: Mist Hunter (v)
 */
public class Card501_018 extends AbstractStarfighter {
    public Card501_018() {
        super(Side.DARK, 4, 3, 3, null, 3, 5, 4, "Mist Hunter", Uniqueness.UNIQUE);
        setLore("Commissioned by a group of Gand venture capitalists headed by Zuckuss. Manufactured by Byblos Drive Yards. Uses repulsor lift technology developed for combat cloud cars.");
        setGameText("May add 2 alien pilots and 3 passengers. Has Ship-Docking Capability. If piloted by Zuckuss, at the start of your turn, may peek at the top card of your reserve deck, may place it on the bottom of your reserve deck. Immune to attrition < 5 if Zuckuss piloting.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.BYBLOS_G1A_TRANSPORT);
        addPersona(Persona.MIST_HUNTER);
        setPilotCapacity(2);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Zuckuss);
        setVirtualSuffix(true);
        setTestingText("Mist Hunter (v)");
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.alien, Filters.pilot);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Zuckuss), 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        final GameState gameState = game.getGameState();
        if(TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
            && GameConditions.hasPiloting(game, self, Filters.Zuckuss)
            && GameConditions.hasReserveDeck(game, playerId)){
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.appendEffect(
                    new PeekAtTopCardOfReserveDeckEffect(action, playerId){
                        @Override
                        protected void cardPeekedAt(final PhysicalCard peekedAtCard) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new YesNoDecision("Do you want to place " + GameUtils.getCardLink(peekedAtCard) + " on bottom of Reserve Deck?") {
                                                @Override
                                                protected void yes() {
                                                    gameState.sendMessage(playerId + " chooses to place card on bottom of Reserve Deck");
                                                    action.appendEffect(
                                                            new PutCardFromReserveDeckOnBottomOfCardPileEffect(action, peekedAtCard, Zone.RESERVE_DECK, true));
                                                }
                                                @Override
                                                protected void no() {
                                                    gameState.sendMessage(playerId + " chooses to not place card on bottom of Reserve Deck");
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
