package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Imperial Square
 */
public class Card7_278 extends AbstractSite {
    public Card7_278() {
        super(Side.DARK, Title.Coruscant_Imperial_Square, Title.Coruscant);
        setLocationDarkSideGameText("Emperor deploys free here. If your moff here, all Imperials are deploy -1 at sites.");
        setLocationLightSideGameText("Force drain +1 here. If you control, Emperor may not deploy to Coruscant.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Emperor, self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial,
                new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.moff)), -1, Filters.site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Emperor,
                new ControlsCondition(playerOnLightSideOfLocation, self), Filters.Coruscant_location));
        return modifiers;
    }
}