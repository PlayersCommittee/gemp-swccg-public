package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNonuniqueStarshipSite;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Star Destroyer: Launch Bay
 */
public class Card4_165 extends AbstractNonuniqueStarshipSite {
    public Card4_165() {
        super(Side.DARK, Title.Launch_Bay, Filters.Star_Destroyer, Uniqueness.RESTRICTED_3);
        setLocationDarkSideGameText("Your TIEs deploy -2 here. You may shuttle, transfer, embark and disembark here for free.");
        setLocationLightSideGameText("Starships captured by Star Destroyer go here and may be Besieged. Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Filter your = Filters.your(playerOnDarkSideOfLocation);
        Filter yourTIEs = Filters.and(your, Filters.TIE);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourTIEs, new PerTIEEvaluator(-2), self));
        modifiers.add(new MayShuttleTransferLandOrTakeOffHereInsteadOfRelatedStarshipForFreeModifier(self, playerOnDarkSideOfLocation));
        modifiers.add(new ShipdocksForFreeModifier(self, Filters.relatedStarshipOrVehicle(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // TODO: Game text (for captured starships and Besieged)
        modifiers.add(new ChangeTractorBeamDestinationModifier(self, Filters.and(Filters.tractor_beam, Filters.attachedTo(Filters.relatedStarshipOrVehicle(self))), self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}