package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Executor: Comm Station
 */
public class Card4_159 extends AbstractUniqueStarshipSite {
    public Card4_159() {
        super(Side.DARK, "Executor: Comm Station", Persona.EXECUTOR);
        setLocationDarkSideGameText("If you occupy with Tarkin, Piett or any admiral, your starships may 'react' to same system.");
        setLocationLightSideGameText("If you control, your starships may 'react' from same system as Executor.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition occupyWithTarkinPiettOrAnyAdmiral = new OccupiesWithCondition(playerOnDarkSideOfLocation, self,
                Filters.or(Filters.Tarkin, Filters.Piett, Filters.admiral));
        Filter yourStarships = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship);
        Filter sameSystem = Filters.sameSystemAs(self, Filters.Executor);

        List<Modifier> modifiers = new LinkedList<Modifier>();

        // TODO: MoveToLocationAsReact????

        // modifiers.add(new MayReactToLocationModifier(self, yourStarships, occupyWithTarkinPiettOrAnyAdmiral, sameSystem));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // TODO: Fix this modifiers.add(new MayMoveAwayAsReactModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship,
                // Filters.at(Filters.sameSystemAs(self, Filters.Executor))), new ControlsCondition(playerOnLightSideOfLocation, self)));
        return modifiers;
    }
}