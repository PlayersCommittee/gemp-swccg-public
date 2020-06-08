package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Blown Clear
 */
public class Card7_222 extends AbstractNormalEffect {
    public Card7_222() {
        super(Side.DARK, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Blown Clear", Uniqueness.UNIQUE);
        setLore("Vader was nearly killed when Han damaged his TIE fighter during a surprise attack in the Death Star trench.");
        setGameText("Deploy on your side of table. During a battle, you may place out of play from hand a copy of any unique (•) card participating in that battle to reduce attrition against you by that card's forfeit value. (Immune to Alter while you occupy a battleground.)");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.canModifyAttritionAgainst(game, playerId)
                && GameConditions.isAttritionRemaining(game, playerId)) {
            final Collection<PhysicalCard> mayBePlacedOutOfPlay = Filters.filter(game.getGameState().getHand(playerId), game,
                    Filters.and(Filters.unique, Filters.sameTitleAs(self, Filters.and(Filters.unique, Filters.participatingInBattle))));
            if (!mayBePlacedOutOfPlay.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place card from hand out of play");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardFromHandEffect(action, playerId, Filters.in(mayBePlacedOutOfPlay)) {
                            @Override
                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                final float forfeitValue = game.getModifiersQuerying().getForfeit(game.getGameState(), selectedCard);
                                action.setActionMsg("Reduce attrition by " + GuiUtils.formatAsString(forfeitValue));
                                // Pay cost(s)
                                action.appendCost(
                                        new PlaceCardOutOfPlayFromOffTableEffect(action, selectedCard));
                                // Perform result(s)
                                action.appendEffect(
                                        new ReduceAttritionEffect(action, playerId, forfeitValue));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OccupiesCondition(playerId, Filters.battleground), Title.Alter));
        return modifiers;
    }
}