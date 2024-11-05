package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: Site
 * Title: Ferfiek Chawa: Reception Area
 */
public class Card304_103 extends AbstractUniqueStarshipSite {
    public Card304_103() {
        super(Side.LIGHT, "Ferfiek Chawa: Reception Area", Persona.FERFIEK_CHAWA, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("While your unique (•) alien here, your Force generation is +1 here (+2 if Broe).");
        setLocationLightSideGameText("Force drain -1 here (if your [CSP] here, Force drain +1 instead).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.MOBILE, Icon.STARSHIP_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter brie = Filters.and(Filters.your(playerOnLightSideOfLocation), Persona.BRIE);
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.unique, Filters.alien)), new ConditionEvaluator(1, 2, new HereCondition(self, brie)), playerOnLightSideOfLocation));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition cspHere = new HereCondition(self, Filters.CSP_character);
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(cspHere), -1, playerOnDarkSideOfLocation));
        modifiers.add(new ForceDrainModifier(self, cspHere, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
