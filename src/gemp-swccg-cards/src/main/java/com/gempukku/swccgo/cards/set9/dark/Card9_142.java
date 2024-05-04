package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractMobileSystem;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: System
 * Title: Death Star II
 */
public class Card9_142 extends AbstractMobileSystem {
    public Card9_142() {
        super(Side.DARK, Title.Death_Star_II, 8, Title.Endor, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLocationDarkSideGameText("X = parsec of current position. Must deploy orbiting Endor. Death Star II locations are immune to revolution. Opponent's Force Drains +3 here unless That Thing's Operational on table.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Death_Star_II_location, Title.Revolution));
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.That_Things_Operational)),
                3, game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }
}