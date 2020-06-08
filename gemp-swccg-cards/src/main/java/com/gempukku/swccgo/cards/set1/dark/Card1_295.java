package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Eisley
 */
public class Card1_295 extends AbstractSite {
    public Card1_295() {
        super(Side.DARK, Title.Mos_Eisley, Title.Tatooine);
        setLocationDarkSideGameText("Your spies, thieves, bounty hunters and smugglers are each power and forfeit +1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourSpiesThievesBountyHuntersAndSmuggersHere = Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.or(Filters.spy, Filters.thief, Filters.bounty_hunter, Filters.smuggler), Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourSpiesThievesBountyHuntersAndSmuggersHere, 1));
        modifiers.add(new ForfeitModifier(self, yourSpiesThievesBountyHuntersAndSmuggersHere, 1));
        return modifiers;
    }
}