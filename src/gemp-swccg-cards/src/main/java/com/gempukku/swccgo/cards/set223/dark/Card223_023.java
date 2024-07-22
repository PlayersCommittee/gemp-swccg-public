package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Imperial
 * Title: TD-110
 */

public class Card223_023 extends AbstractImperial {
    public Card223_023() {
        super(Side.DARK, 3, 2, 2, 2, 3, "TD-110", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setArmor(4);
        setLore("Sandtrooper. Leader.");
        setGameText("If Send A Detachment Down on table, destiny +2 when drawn for destiny. During battle at a Tatooine site, may place Tactical Support out of play from Lost Pile to add 2 to attrition against opponent.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.SANDTROOPER, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, new OnTableCondition(self, Filters.title(Title.Send_A_Detachment_Down)), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TD_110__MODIFY_ATTRITION;
        String opponent = game.getOpponent(playerId);
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isInBattleAt(game, self, Filters.Tatooine_site)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(playerId)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Add 2 to your total attrition");
                // Pay cost(s)
                action.appendCost(
                    new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, Filters.Tactical_Support, false));
                // Perform result(s)
                action.appendEffect(
                    new AddToAttritionEffect(action, opponent, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

}
