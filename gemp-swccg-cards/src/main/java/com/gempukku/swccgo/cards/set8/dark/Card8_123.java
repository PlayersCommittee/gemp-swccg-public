package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Empire's New Order
 */
public class Card8_123 extends AbstractNormalEffect {
    public Card8_123() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Empires_New_Order, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Palpatine's cruel vision of his Empire included the enslavement and subjugation of entire species.");
        setGameText("Deploy on table. Droid Merchant game text is canceled. Also, while none of your ability on table is provided by aliens, retrieve 1 Force after each battle in which opponent's alien is lost (or 2 if Ewok, Elom or operative). (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Droid_Merchant));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)) {
            self.setWhileInPlayData(null);
            return null;
        }
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.alien))
                && GameConditions.isAllAbilityOnTableProvidedBy(game, self, playerId, Filters.not(Filters.alien))) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();
            Boolean inPlayData = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getBooleanValue() : null;
            if (inPlayData == null || !inPlayData) {
                if (!Filters.playersCardsAtLocationMayContributeToForceRetrieval(opponent).accepts(game, game.getGameState().getBattleLocation())) {
                    game.getGameState().sendMessage("Force retrieval will not allowed from " + GameUtils.getCardLink(self) + " due to cards not allowed to contribute to Force retrieval");
                }
                else {
                    self.setWhileInPlayData(new WhileInPlayData(Filters.or(Filters.Ewok, Filters.Elom, Filters.operative).accepts(game, cardLost)));
                }
            }
            return null;
        }
        // Check condition(s)
        if (TriggerConditions.battleEnded(game, effectResult)
                && GameConditions.isAllAbilityOnTableProvidedBy(game, self, playerId, Filters.not(Filters.alien))) {
            Boolean inPlayData = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getBooleanValue() : null;
            if (inPlayData != null) {
                int numForceToRetrieve = inPlayData ? 2 : 1;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Have " + playerId + " retrieve " + numForceToRetrieve + " Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, numForceToRetrieve));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}