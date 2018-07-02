package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tydirium
 */
public class Card8_079 extends AbstractStarfighter {
    public Card8_079() {
        super(Side.LIGHT, 3, 2, 2, null, 2, 3, 4, Title.Tydirium, Uniqueness.UNIQUE);
        setLore("Stolen Imperial Lambda-class shuttle. Supposedly carried parts and technical crew. Delivered General Solo's crack team of Rebel scouts to the forest moon of Endor.");
        setGameText("May deploy (and your characters may deploy aboard) even without presence of Force icons. May add 2 pilots and 6 passengers. While Tydirium is at a system location, your scouts deploy -1 aboard.");
        addIcons(Icon.ENDOR, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(2);
        setPassengerCapacity(6);
        setAlwaysStolen(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithoutPresenceOrForceIconsModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetWithoutPresenceOrForceIconsModifier(self, Filters.and(Filters.your(self), Filters.character), self));
        modifiers.add(new DeployCostAboardModifier(self, Filters.and(Filters.your(self), Filters.scout), new AtCondition(self, Filters.system), -1, self));
        return modifiers;

    }
}
