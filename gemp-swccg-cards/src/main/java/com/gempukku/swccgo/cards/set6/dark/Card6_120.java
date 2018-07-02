package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Quarren
 */
public class Card6_120 extends AbstractAlien {
    public Card6_120() {
        super(Side.DARK, 3, 2, 1, 2, 2, "Quarren");
        setLore("Nicknamed 'squidheads.' Quarren share their watery homeworld with the Mon Calamari. Betrayed their planet to the Empire. Dwell on the past. Rarely look to the future.");
        setGameText("If present at a non-shielded site, your Imperials may deploy there (regardless of location deployment restrictions). When at a planet site, Tactical Support, Imperial Reinforcements and Full Scale Alert are Used Interrupts.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.QUARREN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePresentAtNonShieldedSite = new PresentAtCondition(self, Filters.and(Filters.site, Filters.not(Filters.shielded_location)));
        Condition whileAtPlanetSite = new AtCondition(self, Filters.planet_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsWhenDeployingToLocationModifier(self, Filters.and(Filters.your(self), Filters.Imperial), whilePresentAtNonShieldedSite, Filters.here(self)));
        modifiers.add(new UsedInterruptModifier(self, Filters.or(Filters.Tactical_Support, Filters.Imperial_Reinforcements, Filters.Full_Scale_Alert), whileAtPlanetSite));
        return modifiers;
    }
}
