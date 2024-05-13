package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleInitiatedByCondition;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: First Strike
 */
public class Card7_229 extends AbstractNormalEffect {
    public Card7_229() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.First_Strike, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("'There'll be no escape for the princess this time.'");
        setGameText("Deploy on your side of table. Whenever a battle is initiated, player initiating battle retrieves 1 Force and defender loses 1 Force. Also, during a battle you initiate, each time opponent plays an interrupt, opponent must first use 1 Force. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)) {
            BattleInitiatedResult initiateBattleResult = (BattleInitiatedResult) effectResult;
            String battleInitiator = initiateBattleResult.getPerformingPlayerId();
            String defender = game.getOpponent(battleInitiator);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force and lose 1 Force");
            action.setActionMsg("Make " + battleInitiator + " retrieve 1 Force and " + defender + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, battleInitiator, 1) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Filters.filterActive(game, null, Filters.and(Filters.your(self), Filters.participatingInBattle));
                        }
                        @Override
                        public boolean isDueToInitiatingBattle() {
                            return true;
                        }
                    });
            action.appendEffect(
                    new LoseForceEffect(action, defender, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.opponents(self), Filters.Interrupt),
                new DuringBattleInitiatedByCondition(playerId), 1));
        return modifiers;
    }
}