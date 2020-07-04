package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
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
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: First Light: Reception Area
 */
public class Card501_057 extends AbstractUniqueStarshipSite {
    public Card501_057() {
        super(Side.DARK, "First Light: Reception Area", Persona.FIRST_LIGHT);
        setLocationDarkSideGameText("Your Force generation here is +1 for each of your unique characters here (Margo adds 2 instead).");
        setLocationLightSideGameText("Force drain -1 here (+1 instead if your smuggler here).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_13, Icon.INTERIOR_SITE, Icon.SCOMP_LINK, Icon.MOBILE, Icon.STARSHIP_SITE);
        setTestingText("First Light: Reception Area");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter yourUniqueCharacters = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character, Filters.unique);
        Filter yourUniqueCharactersNotMargo = Filters.and(yourUniqueCharacters, Filters.not(Filters.and(Persona.MARGO)));
        Filter margo = Filters.and(Persona.MARGO);

        // Force Generation +1 for each non-Margo
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), new HereCondition(self, yourUniqueCharactersNotMargo), new HereEvaluator(self, yourUniqueCharactersNotMargo), playerOnDarkSideOfLocation));

        // Force Generation +2 more for Margo
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), new HereCondition(self, margo), 2, playerOnDarkSideOfLocation));

        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition smugglerHere = new HereCondition(self, Filters.smuggler);
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(smugglerHere), -1, playerOnLightSideOfLocation));
        modifiers.add(new ForceDrainModifier(self, smugglerHere, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

}
