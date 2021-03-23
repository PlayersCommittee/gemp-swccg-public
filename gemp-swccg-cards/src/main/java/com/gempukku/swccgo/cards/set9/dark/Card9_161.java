package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Onyx 2
 */
public class Card9_161 extends AbstractStarfighter {
    public Card9_161() {
        super(Side.DARK, 2, 3, 3, null, 4, 3, 4, Title.Onyx_2, Uniqueness.UNIQUE);
        setLore("Part of limited production run of TIE defenders. Testing of the prototype defender indicated the need for a more powerful hyperdrive, which was added for this production model.");
        setGameText("Deploys for free to any mobile system. May deploy with a pilot as a 'react'. May add 1 pilot. Any starship cannon may deploy aboard. Immune to attrition < 4 when Yorr piloting.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER);
        addKeywords(Keyword.ONYX_SQUADRON);
        addModelType(ModelType.TIE_DEFENDER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Yorr);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.mobile_system));
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.starship_cannon, Filters.starship_weapon_that_deploys_on_starfighters), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Yorr), 4));
        return modifiers;
    }
}
