package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Offworld Jawas
 */
public class Card216_040 extends AbstractAlien {
    public Card216_040() {
        super(Side.LIGHT, 3, 4, 3, 2, 4, "Offworld Jawas", Uniqueness.RESTRICTED_2);
        setLore("Jawa. Scavenger. Thief.");
        setGameText("Draws one battle destiny if unable to otherwise. If you just verified opponent's Reserve Deck, may use 1 Force to search that Reserve Deck and place one device, weapon, or unpiloted starship there in Lost Pile; reshuffle. If none there, opponent loses 1 Force.");
        setSpecies(Species.JAWA);
        addKeywords(Keyword.SCAVENGER, Keyword.THIEF);
        addIcons(Icon.VIRTUAL_SET_16);
        addIcon(Icon.WARRIOR, 3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        Filter filter = Filters.or(Filters.weapon, Filters.device, Filters.and(Filters.unpiloted, Filters.starship));

        GameTextActionId gameTextActionId = GameTextActionId.OFFWORLD_JAWAS__SEARCH_RESERVE_DECK;

        if (TriggerConditions.justVerifiedOpponentsReserveDeck(game, effectResult, playerId)
                && GameConditions.canSearchOpponentsReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search opponent's Reserve Deck");
            action.setActionMsg("Search opponent's Reserve Deck for a device, weapon, or unpiloted starship");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1)
            );
            action.appendEffect(
                    new ChooseCardsFromReserveDeckEffect(action, playerId, opponent, 1, 1, filter) {
                        @Override
                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                            if (!selectedCards.isEmpty()) {
                                PhysicalCard selectedCard = selectedCards.iterator().next();
                                if (selectedCard != null) {
                                    action.appendEffect(
                                            new PutCardFromReserveDeckOnTopOfCardPileEffect(action, selectedCard, Zone.LOST_PILE, false)
                                    );
                                    action.appendEffect(new ShuffleReserveDeckEffect(action, opponent));
                                }
                            } else {
                                action.appendEffect(new ShuffleReserveDeckEffect(action, opponent));
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, 1)
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
