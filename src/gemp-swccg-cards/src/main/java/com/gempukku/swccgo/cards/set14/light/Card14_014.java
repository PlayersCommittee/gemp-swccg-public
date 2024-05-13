package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Jerus Jannick
 */
public class Card14_014 extends AbstractRepublic {
    public Card14_014() {
        super(Side.LIGHT, 2, 3, 4, 2, 3, "Jerus Jannick", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Part of Amidala's Royal Naboo Security Forces, Jerus has been trained as a protector of royalty, and is determined that his responsibility be carried out professionally.");
        setGameText("Deploys -2 to same site as Amidala. While in a battle with Amidala or Leia, your leaders present may not be targeted by weapons, and Jerus may be forfeited to satisfy all battle damage and attrition against you.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_NABOO_SECURITY);
        addPersona(Persona.JERUS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.sameSiteAs(self, Filters.Amidala)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.leader, Filters.present(self)),
                new InBattleWithCondition(self, Filters.or(Filters.Amidala, Filters.Leia))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttritionAndBattleDamage(game, playerId, self)
                && Filters.Jerus.accepts(game, self)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Amidala, Filters.Leia))) {
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
