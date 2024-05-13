package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesUsingLandspeedOnlyDuringDeployPhaseModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Tzizvvt
 */
public class Card2_022 extends AbstractAlien {
    public Card2_022() {
        super(Side.LIGHT, 3, 3, 2, 1, 1, Title.Tzizvvt, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("This male Brizzit, from the planet Jandoon, is hiding from the Imperials on the remote world Tatooine. He hopes to procure passage to the Outer Rim before the Empire finds him.");
        setGameText("May 'fly' (move) only during your deploy phase, up to two sites away, for 1 Force. Power +1 at Trash Compactor, any Dagobah site or same site as Dark Waters or Tarkin.");
        addIcons(Icon.A_NEW_HOPE);
        setSpecies(Species.BRIZZIT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        modifiers.add(new MovesUsingLandspeedOnlyDuringDeployPhaseModifier(self));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Trash_Compactor, Filters.Dagobah_site,
                Filters.sameSiteAs(self, Filters.or(Filters.Dark_Waters, Filters.Tarkin)))), 1));
        return modifiers;
    }
}
