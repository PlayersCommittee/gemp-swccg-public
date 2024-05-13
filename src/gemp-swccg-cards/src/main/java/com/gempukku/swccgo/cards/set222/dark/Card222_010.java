package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: First Order
 * Title: Kylo, Master Of The Knights Of Ren
 */
public class Card222_010 extends AbstractFirstOrder {
    public Card222_010() {
        super(Side.DARK, 1, 6, 6, 5, 7, "Kylo, Master Of The Knights Of Ren", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("Knight of Ren. Leader.");
        setGameText("[Pilot] 3. While in battle alone: your total power here is +3, battle destiny draws may not be added and, " +
                "if defending, once per game may [upload] an Interrupt (except Ghhhk). Immune to attrition < 4 (< 5 if alone).");
        addPersona(Persona.KYLO);
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.LEADER, Keyword.KNIGHT_OF_REN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition inBattleAlone = new AndCondition(new InBattleCondition(self), new AloneCondition(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), inBattleAlone, 3, self.getOwner()));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.any, inBattleAlone));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 5, new AloneCondition(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.KYLO_MASTER_OF_THE_KNIGHTS_OF_REN__UPLOAD_INTERRUPT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isAlone(game, self)
                && GameConditions.isOpponentsTurn(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Interrupt into hand from Reserve Deck");
            action.setActionMsg("Take an Interrupt (except Ghhhk) into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Interrupt, Filters.not(Filters.Ghhhk)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
