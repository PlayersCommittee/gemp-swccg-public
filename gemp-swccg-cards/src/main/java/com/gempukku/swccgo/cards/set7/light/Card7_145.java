package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasMatchingPilotAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 10
 */
public class Card7_145 extends AbstractStarfighter {
    public Card7_145() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, Title.Red_10, Uniqueness.UNIQUE);
        setLore("Theron Nett's X-wing. Assigned to be Red Leader's wingman during the second attack run on the Death Star.");
        setGameText("Deploys free to Ord Mantell. May add 1 pilot. Proton Torpedoes deploy and fire free aboard. Immune to attrition < 4 when matching pilot aboard.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToSystemModifier(self, Title.Ord_Mantell));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Proton_Torpedoes, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.Proton_Torpedoes, Filters.attachedTo(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasMatchingPilotAboardCondition(self), 4));
        return modifiers;
    }
}
