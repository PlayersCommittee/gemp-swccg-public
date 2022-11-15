package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.ControlsEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: System
 * Title: Alderaan
 */
public class Card1_281 extends AbstractSystem {
    public Card1_281() {
        super(Side.DARK, Title.Alderaan, 2, ExpansionSet.PREMIERE, Rarity.R1);
        setLocationDarkSideGameText("If you control, Force drain +1 here for each Death Star site on table also in your control.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self),
                new ControlsEvaluator(playerOnDarkSideOfLocation, Filters.Death_Star_site), playerOnDarkSideOfLocation));
        return modifiers;
    }
}