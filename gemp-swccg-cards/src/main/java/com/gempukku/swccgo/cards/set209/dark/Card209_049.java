package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 •Jedha: Jedha City [Spaceport Prefect's Office (V)]
 [Special Edition - F]
 LOCATION - SITE
 DARK (1): May be deployed instead of Alderaan by Set Your Course For Alderaan.
 LIGHT (1): Baze, Chirrut, and Saw deploy -1 here.
 [Planet] [Exterior] [Set 9]
*/

public class Card209_049 extends AbstractSite {
    public Card209_049() {
        super(Side.DARK, Title.Jedha_City, Title.Jedha);
        setLocationDarkSideGameText("May be deployed instead of Alderaan by Set Your Course For Alderaan.");
        setLocationLightSideGameText("Baze, Chirrut, and Saw deploy -1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_9, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    // NOTE: The "May be deployed instead of Alderaan by Set Your Course For Alderaan." portion of the text has been
    // implemented on the SYCFA objective.

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.Baze, Filters.Chirrut, Filters.Saw), -1, self));
        return modifiers;
    }
}
