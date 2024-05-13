package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationsByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Echo Corridor
 */
public class Card3_146 extends AbstractSite {
    public Card3_146() {
        super(Side.DARK, "Hoth: Echo Corridor", Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLocationDarkSideGameText("If you occupy, you may raise all of your converted Hoth locations to the top.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.occupies(game, playerOnDarkSideOfLocation, self)
                && GameConditions.canSpotConvertedLocation(game, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Hoth_location))) {

            Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.Hoth_location, Filters.canBeConvertedByRaisingYourLocationToTop(playerOnDarkSideOfLocation)));

            if (!locations.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                action.setText("Raise your converted Hoth locations to the top");

                action.appendEffect(
                        new ConvertLocationsByRaisingToTopEffect(action, locations, true));

                return Collections.singletonList(action);
            }
        }

        return null;
    }
}