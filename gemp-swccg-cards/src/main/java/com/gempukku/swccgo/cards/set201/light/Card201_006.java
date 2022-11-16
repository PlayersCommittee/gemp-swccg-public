package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

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
        super(Side.LIGHT, 2, 2, 1, 4, "R2-D2 (Artoo-Detoo)", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setAlternateDestiny(5);
        setVirtualSuffix(true);
        setLore("Fiesty. Loyal. Heroic. Insecure. Rebel spy. Excels at trouble. Incorrigible counterpart of a mindless philosopher. Has picked up a slight flutter. A bit eccentric.");
        setGameText("While aboard a starfighter, adds 2 to power, maneuver, and hyperspeed. While with a Scomp link, adds one [Light Side] icon here. Immune to Fire Extinguisher and Restraining Bolt.");
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
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        return modifiers;
    }
}
