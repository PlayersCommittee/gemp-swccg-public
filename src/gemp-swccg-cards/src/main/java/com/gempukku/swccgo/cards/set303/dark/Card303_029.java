package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Starship
 * Subtype: Starfighter
 * Title: TXR-711 Attack Ship
 */
public class Card303_029 extends AbstractStarfighter {
    public Card303_029() {
        super(Side.DARK, 3, 3, 3, null, 4, 4, 4, "TXR-711 Attack Ship", Uniqueness.UNRESTRICTED, ExpansionSet.SA, Rarity.C);
        setLore("The TXR-711 Attack Ship is a new addition from Arx Starship Acquisitions.");
        setGameText("May add 2 pilots and 2 passengers. Starship Cannons may deploy (and fire free) aboard.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.XTR711);
        setPilotCapacity(2);
        setPassengerCapacity(2);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        Filter quadLaserCannonAndSurfaceDefenseCannon = Filters.or(Filters.starship_cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), quadLaserCannonAndSurfaceDefenseCannon), self));
        modifiers.add(new DeploysFreeToTargetModifier(self, quadLaserCannonAndSurfaceDefenseCannon, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter quadLaserCannonAndSurfaceDefenseCannon = Filters.or(Filters.starship_cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(quadLaserCannonAndSurfaceDefenseCannon, Filters.attachedTo(self))));
        return modifiers;
    }
}
