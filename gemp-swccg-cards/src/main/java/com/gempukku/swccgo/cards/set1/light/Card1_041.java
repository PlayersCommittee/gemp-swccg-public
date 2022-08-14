package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Vaporator
 */
public class Card1_041 extends AbstractDevice {
    public Card1_041() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Vaporator);
        setLore("Essential for life on desert planets. Condenses water vapor from atmosphere. Has purification filters and coolant tanks. Protects against drought and harsh conditions.");
        setGameText("Use 1 Force to deploy on any Tatooine site. Cannot be moved. Protects all characters at same site, or an adjacent site, from Gravel Storm.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Tatooine_site;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.character, Filters.atSameOrAdjacentSite(self)), Title.Gravel_Storm));
        return modifiers;
    }
}