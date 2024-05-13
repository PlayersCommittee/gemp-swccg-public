package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.MayMoveCondition;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.conditions.NotCondition;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Club Antonia Bouncer
 */
public class Card304_053 extends AbstractAlien {
    public Card304_053() {
        super(Side.LIGHT, 4, 3, 0, 1, 2, "Club Antonia Bouncer", Uniqueness.RESTRICTED_2, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Chevin smuggler. One of Jabba's few truly loyal associates. Keeps Jabba informed as to the various plots against his life. Leader.");
        setGameText("Opponent's spies, gamblers and thieves may not deploy or move to same site. When with Jabba in a battle, power +2 and, if forfeited, may satisfy all remaining battle damage and attrition against you.");
		addKeywords(Keyword.CLAN_TIURE);
    }
	
	@Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Club_Antonia_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentsCSPImperials = Filters.and(Filters.opponents(self), Filters.and(Filters.CSP, Filters.Imperial));
        Filter sameSite = Filters.sameSite(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, opponentsCSPImperials, sameSite));
        modifiers.add(new MayNotMoveToLocationModifier(self, opponentsCSPImperials, sameSite));
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 4));
        modifiers.add(new MayNotMoveModifier(self, new NotCondition(new MayMoveCondition(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttritionAndBattleDamage(game, playerId, self)
                && GameConditions.isInBattleWith(game, self, Filters.Clan_Tiure)) {
            boolean cannotSatisfyAttrition = game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), self);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            if (cannotSatisfyAttrition)
                action.setText("Forfeit to satisfy all battle damage");
            else
                action.setText("Forfeit to satisfy all battle damage and attrition");
            // Pay cost(s)
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            action.setActionMsg(null);
            // Perform result(s)
            if (cannotSatisfyAttrition)
                action.appendEffect(
                        new SatisfyAllBattleDamageEffect(action, playerId));
            else
                action.appendEffect(
                        new SatisfyAllBattleDamageAndAttritionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
