package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Nick Of Time
 */
public class Card7_072 extends AbstractNormalEffect {
    public Card7_072() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Nick Of Time", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Luke barely escaped being crushed by the AT-AT's massive footpad during the Battle of Hoth.");
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
                    Filters.and(Filters.unique, Filters.sameTitleAs(self, Filters.and(Filters.unique, Filters.participatingInBattle)), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)));
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