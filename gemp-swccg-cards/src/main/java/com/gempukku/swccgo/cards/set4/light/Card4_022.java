package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Encampment
 */
public class Card4_022 extends AbstractNormalEffect {
    public Card4_022() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Encampment", Uniqueness.DIAMOND_1);
        setLore("Pirates, smugglers and Rebels operate from tiny camps which can be set up quickly and relocated at a moment's notice. A surprise visitor made Luke an unhappy camper.");
        setGameText("Use 2 Force to deploy on any exterior planet site where you have a Rebel or alien. You may deploy characters at this site, even without presence, regardless of location deployment restrictions.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.exterior_planet_site, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.alien))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.and(Filters.your(self), Filters.character), Filters.sameSite(self)));
        return modifiers;
    }
}