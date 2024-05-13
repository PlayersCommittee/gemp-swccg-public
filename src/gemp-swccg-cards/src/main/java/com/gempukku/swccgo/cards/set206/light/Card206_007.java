package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Starship
 * Subtype: Starfighter
 * Title: Rogue One
 */
public class Card206_007 extends AbstractStarfighter {
    public Card206_007() {
        super(Side.LIGHT, 3, 3, 2, null, 3, 4, 5, Title.Rogue_One, Uniqueness.UNIQUE, ExpansionSet.SET_6, Rarity.V);
        setGameText("May add 2 pilots and 4 passengers. May deploy with a pilot to a system without presence or Force icons. Spies deploy -1 aboard. Immune to attrition < 6 at opponent's system (unless a Dark Jedi here).");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.ROGUE_SQUADRON);
        addModelType(ModelType.ZETA_CLASS_TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationWithoutPresenceOrForceIconsModifier(self, Filters.system));
        modifiers.add(new MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier(self, Filters.system));
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.spy, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, Filters.spy, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new AndCondition(new AtCondition(self, Filters.and(Filters.opponents(self), Filters.system)),
                new UnlessCondition(new HereCondition(self, Filters.Dark_Jedi))), 6));
        return modifiers;
    }
}
