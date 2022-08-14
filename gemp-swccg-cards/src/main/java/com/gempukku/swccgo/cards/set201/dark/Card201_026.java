package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Imperial
 * Title: Lt. Pol Treidum (V)
 */
public class Card201_026 extends AbstractImperial {
    public Card201_026() {
        super(Side.DARK, 3, 2, 2, 1, 5, "Lt. Pol Treidum", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Gantry officer charged with maintaining magnetic field, atmosphere and security in Death Star docking bay. After mission on Ralltiir, paranoid about infiltration by Rebel spies.");
        setGameText("[Pilot] 1. When deployed (or leaves table), may draw top card of Reserve Deck or Used Pile. During battle, attrition against you is -1 for each ISB agent here (limit -3). Your spies here (unless Undercover) are immune to Double Agent.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new AttritionModifier(self, new InBattleCondition(self), new NegativeEvaluator(new MaxLimitEvaluator(new HereEvaluator(self, Filters.ISB_agent), 3)), playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.spy, Filters.not(Filters.undercover_spy), Filters.here(self)), Title.Double_Agent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasUsedPile(game, playerId))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw top card from Reserve Deck or Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, playerId, Filters.or(Zone.RESERVE_DECK, Zone.USED_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Draw top card from " + cardPile.getHumanReadable());
                            // Perform result(s)
                            if (cardPile == Zone.RESERVE_DECK) {
                                action.appendEffect(
                                        new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                            }
                            else {
                                action.appendEffect(
                                        new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasUsedPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId) {
                @Override
                public String getTriggerIdentifier(boolean useBlueprintId) {
                    // need to use the permanentCardId() instead of getCardId() just in case he is put into used pile from table and then draws himself
                    // which gives him a different cardId so he triggers again
                    // include "treidum" so that it doesn't conflict with anything with a cardId() matching this permanentCardId()
                    return useBlueprintId ?
                            "treidum"+self.getPermanentCardId()+"|"+self.getOwner()+"|"+self.getPermanentCardId()+"|"+ GameTextActionId.OTHER_CARD_ACTION_DEFAULT :
                            "treidum"+self.getBlueprintId(true)+"|"+self.getOwner()+"|"+self.getPermanentCardId()+"|"+ GameTextActionId.OTHER_CARD_ACTION_DEFAULT;
                }

                @Override
                public boolean isSingletonTrigger() {
                    return true;
                }
            };
            action.setText("Draw top card from Reserve Deck or Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, playerId, Filters.or(Zone.RESERVE_DECK, Zone.USED_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Draw top card from " + cardPile.getHumanReadable());
                            // Perform result(s)
                            if (cardPile == Zone.RESERVE_DECK) {
                                action.appendEffect(
                                        new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                            }
                            else {
                                action.appendEffect(
                                        new DrawCardIntoHandFromUsedPileEffect(action, playerId));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
