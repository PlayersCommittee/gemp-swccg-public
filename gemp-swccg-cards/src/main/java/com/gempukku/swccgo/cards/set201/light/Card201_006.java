package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo) (V)
 */
public class Card201_006 extends AbstractDroid {
    public Card201_006() {
        super(Side.LIGHT, 2, 3, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE);
        setAlternateDestiny(5);
        setVirtualSuffix(true);
        setLore("Fiesty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("While present with a Scomp link, adds one [Light Force] icon here. While aboard a starfighter, adds 2 to power, maneuver, and hyperspeed. Immune to Fire Extinguisher.");
        addPersona(Persona.R2D2);
        addModelType(ModelType.ASTROMECH);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starfighterAboard = Filters.and(Filters.starfighter, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), new PresentAtScompLink(self), Icon.LIGHT_FORCE, 1));
        modifiers.add(new PowerModifier(self, starfighterAboard, 2));
        modifiers.add(new ManeuverModifier(self, starfighterAboard, 2));
        modifiers.add(new HyperspeedModifier(self, starfighterAboard, 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Fire_Extinguisher));
        return modifiers;
    }
}
