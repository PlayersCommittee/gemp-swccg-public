package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: System
 * Title: Aquaris
 */
public class Card9_056 extends AbstractSystem {
    public Card9_056() {
        super(Side.LIGHT, "Aquaris", 4, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLocationLightSideGameText("If you control with a starfighter, opponent's Dreadnaught-class cruisers are deploy +3 and your Force generation here is +3.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition controlWithStarfighter = new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.starfighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation),
                Filters.Dreadnaught_class_cruisers), controlWithStarfighter, 3));
        modifiers.add(new ForceGenerationModifier(self, controlWithStarfighter, 3, playerOnLightSideOfLocation));
        return modifiers;
    }
}