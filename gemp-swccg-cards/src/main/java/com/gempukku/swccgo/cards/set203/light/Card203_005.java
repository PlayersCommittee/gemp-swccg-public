package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Endor Commando Team
 */
public class Card203_005 extends AbstractRebel {
    public Card203_005() {
        super(Side.LIGHT, 1, 5, 8, 1, 5, "Endor Commando Team", Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setArmor(4);
        setLore("General Solo's strike team was made up of the Alliance's finest ground troops. Scout troopers.");
        setGameText("Deploys -1 to Endor. During battle, your other Rebels present with Endor Commando Team may not have their forfeit reduced unless this card 'hit'. Each time this character is 'hit', it is cumulatively power -2 until end of turn.");
        addIcons(Icon.ENDOR, Icon.WARRIOR, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.SCOUT, Keyword.TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Endor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Rebel, Filters.presentWith(self)),
                new AndCondition(new InBattleCondition(self), new UnlessCondition(new HitCondition(self)))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHit(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make power -2");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " power -2 until end of turn");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfTurnEffect(action, self, -2, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
