package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedBySpecificWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllAttritionEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: K'vin J. Lawdogg, Attorney-At-Law
 */
public class Card304_026 extends AbstractAlien {
    public Card304_026() {
        super(Side.DARK, 3, 2, 1, 2, 3, "K'vin J. Lawdogg, Attorney-At-Law", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Strange wanderer. Rumored to have sacrificed himself in battle so that his comrades could live on. Survived and now stranded on Tatooine. He misses everything he'll never be.");
        setGameText("While K'vin is in battle, your other characters present with him may not be targeted by opponent's weapons. Place K'vin in Used Pile if he was just 'hit'. If in a battle with Thran may be sacrificed to satisify all attrition.");
        addIcons(Icon.CSP);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedBySpecificWeaponsModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.character,
                Filters.presentWith(self)), new InBattleCondition(self), Filters.and(Filters.opponents(self), Filters.weapon)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHit(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
	
	@Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttrition(game, playerId, self)
                && GameConditions.isAtLocation(game, self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Thran)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to satisfy all attrition");
            // Pay cost(s)
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new SatisfyAllAttritionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
