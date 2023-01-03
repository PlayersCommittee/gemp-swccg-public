package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Dr. Chelli Lona Aphra
 */
public class Card209_035 extends AbstractAlien {
    public Card209_035() {
        super(Side.DARK, 3, 3, 3, 3, 3, "Dr. Chelli Lona Aphra", Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLore("Female information broker, spy, and thief. Trade Federation.");
        setGameText("[Pilot]1. Deploys free to an unoccupied site. Your droids are deploy -1 here. When deployed, may search your Lost Pile and move one card there to the top of that pile (if that card is a droid, may retrieve it into hand).");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.FEMALE, Keyword.INFORMATION_BROKER, Keyword.SPY, Keyword.THIEF);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.unoccupied, Filters.site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.droid), -1, Filters.here(self)));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DR_CHELLI_LONA_APHRA__SEARCH_LOST_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search your Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new ChooseCardFromPileEffect(action, playerId, Zone.LOST_PILE, playerId) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on top of " + Zone.LOST_PILE.getHumanReadable();
                        }
                        @Override
                        protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                            if (selectedCard != null) {
                                String cardInfo = GameUtils.getCardLink(selectedCard);
                                action.setActionMsg("Move " + cardInfo + " to the top of " + Zone.LOST_PILE.getHumanReadable());
                                action.appendEffect(
                                        new PutCardFromLostPileOnTopOfCardPileEffect(action, selectedCard, Zone.LOST_PILE, false));

                                if (Filters.droid.accepts(game, selectedCard)) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new YesNoDecision("Do you want retrieve " + GameUtils.getCardLink(selectedCard) + " into hand?") {
                                                        @Override
                                                        protected void yes() {
                                                            action.setActionMsg("Retrieve " + GameUtils.getCardLink(selectedCard) + " into hand");
                                                            action.appendEffect(
                                                                    new RetrieveCardIntoHandEffect(action, playerId, false));
                                                        }
                                                        @Override
                                                        protected void no() {
                                                            game.getGameState().sendMessage(playerId + " chooses to not to retrieve " + GameUtils.getCardLink(selectedCard) + " into hand");
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

}
