package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scythe Squadron TIE
 */
public class Card9_171 extends AbstractStarfighter {
    public Card9_171() {
        super(Side.DARK, 4, 4, 1, null, 4, null, 3, "Scythe Squadron TIE", Uniqueness.RESTRICTED_3);
        setLore("Scythe Squadron TIE fighters are modified TIE/lns assigned to defend the second Death Star during construction. Their pilots fly frequent training missions within the Death Star.");
        setGameText("Deploy -2 to Death Star II or Endor. Permanent pilot provides ability of 2 and adds 1 to power and maneuver.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.SCYTHE_SQUADRON);
        addModelType(ModelType.TIE_LN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Deploys_at_Death_Star_II, Filters.Deploys_at_Endor)));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 1));
                        modifiers.add(new ManeuverModifier(self, 1));
                        return modifiers;
                    }});
    }
}
