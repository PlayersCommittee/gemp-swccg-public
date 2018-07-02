package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Stormtrooper
 */
public class Card1_194 extends AbstractImperial {
    public Card1_194() {
        super(Side.DARK, 1, 1, 1, 1, 2, "Stormtrooper");
        setLore("One of the countless elite shock troops totally loyal to the Emperor. Unquestioningly follows orders. Willing to sacrifice their lives to accomplish a mission. First-strike force.");
        setGameText("Deploys free to same site as one of your Imperials with ability > 2.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Imperial, Filters.abilityMoreThan(2)))));
        return modifiers;
    }
}
