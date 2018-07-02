package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Navy Trooper
 */
public class Card8_107 extends AbstractImperial {
    public Card8_107() {
        super(Side.DARK, 3, 2, 1, 1, 3, "Navy Trooper");
        setLore("Navy troopers are assigned to defend key installations. Trained to protect against alien species and other insurgents.");
        setGameText("Deploy -1 to a mobile site (except on Cloud City). When defending a battle, power +1 (or +2 if also at an interior site). When in battle against an operative, prevents opponent from drawing more than one battle destiny.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.and(Filters.mobile_site, Filters.not(Filters.Cloud_City_site))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), new ConditionEvaluator(1, 2, new AtCondition(self, Filters.interior_site))));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, new InBattleWithCondition(self, Filters.and(Filters.opponents(self), Filters.operative)),
                1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
