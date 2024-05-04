package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Starship
 * Subtype: Starfighter
 * Title: Liswarr
 */
public class Card218_023 extends AbstractStarfighter {
    public Card218_023() {
        super(Side.LIGHT, 2, 5, 5, 5, null, 5, 5, "Liswarr", Uniqueness.UNIQUE, ExpansionSet.SET_18, Rarity.V);
        setGameText("May add 1 pilot. Permanent pilot provides ability of 2. While Wookiee Homestead on table: deploy -1, power +2, immune to attrition < 5 and, if Liswarr at Kashyyyk, your total power is +2 at Kashyyyk sites.");
        addIcons(Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.EPISODE_I, Icon.VIRTUAL_SET_18);
        addModelType(ModelType.AUZITUCK_GUNSHIP);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.Wookiee_Homestead), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition wookieeHomesteadOnTableCondition = new OnTableCondition(self, Filters.Wookiee_Homestead);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, wookieeHomesteadOnTableCondition, 2));
        modifiers.add(new TotalPowerModifier(self, Filters.Kashyyyk_site, new AndCondition(wookieeHomesteadOnTableCondition, new AtCondition(self, Filters.title("Liswarr"), Title.Kashyyyk)), 2, self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, wookieeHomesteadOnTableCondition,5));
        return modifiers;
    }
}
