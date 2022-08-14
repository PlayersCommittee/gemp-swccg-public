package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Location
 * Subtype: Site
 * Title: Dantooine: Base - Operations Center
 */
public class Card601_257 extends AbstractSite {
    public Card601_257() {
        super(Side.LIGHT, "Dantooine: Base - Operations Center", Title.Dantooine);
        setLocationDarkSideGameText("While More Dangerous Than You Realize on table, you must control three Dantooine locations to flip it back, and Dantooine system gains one [Dark Side Force].");
        setLocationLightSideGameText("Your T-47s may move as a 'react' to Dantooine sites. Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.Dantooine_system, new OnTableCondition(self, Filters.More_Dangerous_Than_You_Realize), Icon.DARK_FORCE, 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.More_Dangerous_Than_You_Realize, ModifyGameTextType.LEGACY__MORE_DANGEROUS_THAN_YOU_REALIZE__REQUIRES_THREE_SITES_TO_FLIP_BACK));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a T-47 as a react", playerOnLightSideOfLocation, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.T_47), Filters.Dantooine_site));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}