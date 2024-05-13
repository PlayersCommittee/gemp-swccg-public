package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
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
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Combat
 * Title: Rogue 4
 */
public class Card7_154 extends AbstractCombatVehicle {
    public Card7_154() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 4, 5, Title.Rogue_4, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Enclosed snowspeeder piloted by Derek 'Hobbie' Klivian. Wingman of Rogue Leader at the Battle of Hoth. Laser cannons modified for improved recharge rate.");
        setGameText("May add 2 pilots or passengers. May move as a 'react' only to Hoth sites. Dual Laser Cannon deploys and fires free aboard. Immune to attrition < 3 when Hobbie piloting.");
        addModelType(ModelType.T_47);
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Hobbie);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Dual_Laser_Cannon, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.Hoth_site));
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.Dual_Laser_Cannon, Filters.attachedTo(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Hobbie), 3));
        return modifiers;
    }
}
