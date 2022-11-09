package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Capital
 * Title: Avenger
 */
public class Card4_166 extends AbstractCapitalStarship {
    public Card4_166() {
        super(Side.DARK, 1, 8, 8, 6, null, 3, 9, Title.Avenger, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Key starship used to subjugate Outer Rim worlds. Reassigned to Death Squadron under the command of Captain Needa. Communications ship at the Battle of Endor.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 2. Subtract 2 from asteroid destiny draws targeting Avenger.");
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        addKeywords(Keyword.DEATH_SQUADRON);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachAsteroidDestinyModifier(self, -2, Filters.Avenger));
        return modifiers;
    }
}
