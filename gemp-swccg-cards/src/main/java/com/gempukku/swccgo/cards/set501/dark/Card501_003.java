package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfCardPileEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//•Fifth Brother [TBD (V)] 2
//[TBD - TBD]
//Lore: Inquisitor.
//DARK - CHARACTER - IMPERIAL
//POWER 4 ABILITY 5 FORCE-SENSITIVE
//Text: Power +3 and defense value -2 while with a Jedi, Padawan, or 'Hatred' card. Whenever you initiate battle here, may peek at the top two cards of your Reserve Deck and place one in Used Pile.
//DEPLOY 4 FORFEIT 6
//[Pilot] [Warrior] [Set 13]

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Imperial
 * Title: Fifth Brother
 */
public class Card501_003 extends AbstractImperial {
    public Card501_003() {
        super(Side.DARK, 2, 4, 4, 5, 6, "Fifth Brother", Uniqueness.UNIQUE);
        setLore("Inquisitor");
        setGameText("Power +3 and defense value -2 while with a Jedi, Padawan, or 'Hatred' card. Whenever you initiate battle here, may peek at the top two cards of your Reserve Deck and place one in Used Pile.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.INQUISITOR);
        setTestingText("Fifth Brother");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        WithCondition withJediPadawanOrHatredCardCondition = new WithCondition(self, Filters.or(Filters.Jedi, Filters.padawan, Filters.hasStacked(Filters.hatredCard)));
        modifiers.add(new PowerModifier(self, withJediPadawanOrHatredCardCondition, 3));
        modifiers.add(new DefenseValueModifier(self, withJediPadawanOrHatredCardCondition, -2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.here(self))
                && GameConditions.hasReserveDeck(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Peek at top 2 cards of Reserve Deck");
            action.appendEffect(
                    new PeekAtTopCardsOfCardPileEffect(action, playerId, playerId, Zone.RESERVE_DECK, 2) {
                        @Override
                        protected void cardsPeekedAt(final List<PhysicalCard> peekedAtCards) {
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId, new ArbitraryCardsSelectionDecision("Choose card in place in Used Pile", peekedAtCards, 1, 1) {
                                        @Override
                                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                                List<PhysicalCard> selectedCards = getSelectedCardsByResponse(result);
                                                if (!selectedCards.isEmpty()) {
                                                    action.appendEffect(
                                                            new PutCardFromReserveDeckOnTopOfCardPileEffect(action, selectedCards.get(0), Zone.USED_PILE, true)
                                                    );
                                                }
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
