package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Civil Disorder (V)
 */
public class Card200_037 extends AbstractNormalEffect {
    public Card200_037() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Civil_Disorder, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Attention. This is Lando Calrissian. The Empire has taken control of the city. I advise everyone to leave before more Imperial troops arrive.'");
        setGameText("Deploy on table. You may not play Rebel Barrier. Senators are deploy +2. Whenever you initiate a battle, may retrieve your topmost non-[Permanent Weapon], non-[Maintenance] character. May place the top card of your Reserve Deck in Lost Pile to place a combat card in owner's Lost Pile. Immune to Alter.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Rebel_Barrier, playerId));
        modifiers.add(new DeployCostModifier(self, Filters.senator, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CIVIL_DISORDER__RETRIEVE_TOPMOST_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve topmost character");
            action.setActionMsg("Retrieve topmost non-[Permanent Weapon], non-[Maintenance] character");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, true, Filters.and(Filters.character, Filters.not(Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)))) {
                        @Override
                        public boolean isDueToInitiatingBattle() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        final PhysicalCard topCardOfReserveDeck = game.getGameState().getTopOfReserveDeck(playerId);
        if (topCardOfReserveDeck != null) {
            final Collection<PhysicalCard> combatCards = Filters.filterStacked(game, Filters.and(Filters.combatCard, Filters.stackedOn(self, Filters.any)));
            if (!combatCards.isEmpty()) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place a combat card in Lost Pile");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, Filters.any, Filters.in(combatCards)) {
                            @Override
                            public String getChoiceText(int numCardsToChoose) {
                                return "Choose combat card" + GameUtils.s(numCardsToChoose);
                            }
                            @Override
                            protected void cardSelected(final PhysicalCard combatCard) {
                                // Pay cost(s)
                                action.appendCost(
                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(action, topCardOfReserveDeck, Zone.LOST_PILE, true));
                                // Perform result(s)
                                action.appendEffect(
                                        new PutStackedCardInLostPileEffect(action, playerId, combatCard, true));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}