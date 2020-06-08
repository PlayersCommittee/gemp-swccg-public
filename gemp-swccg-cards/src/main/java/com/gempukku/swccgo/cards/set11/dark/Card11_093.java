package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Espa
 */
public class Card11_093 extends AbstractSite {
    public Card11_093() {
        super(Side.DARK, Title.Mos_Espa, Title.Tatooine);
        setLocationDarkSideGameText("Your smugglers and thieves are power and forfeit +1 here.");
        setLocationLightSideGameText("Unless your smuggler or thief here, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourSmugglersAndThievesHere = Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.or(Filters.smuggler, Filters.thief), Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourSmugglersAndThievesHere, 1));
        modifiers.add(new ForfeitModifier(self, yourSmugglersAndThievesHere, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition yourSmugglerOrThiefHere = new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.or(Filters.smuggler, Filters.thief)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(yourSmugglerOrThiefHere), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}