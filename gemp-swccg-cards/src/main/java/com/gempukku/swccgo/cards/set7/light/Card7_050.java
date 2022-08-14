package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Wookiee
 */
public class Card7_050 extends AbstractAlien {
    public Card7_050() {
        super(Side.LIGHT, 2, 4, 4, 1, 4, "Wookiee");
        setLore("Wookiees are known to be fierce warriors. Combine high technology with a primitive lifestyle. Escaped Imperial slavery after the Battle of Endor.");
        setGameText("Power +1 at a jungle, forest or Kashyyyk site. Also power +1 at same site as any Imperial. Wookiee Strangle is a Used Interrupt. When Bowcaster is deployed on or fired by this Wookiee, X=1. Adds 3 to destiny of each of your bowcasters.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.jungle, Filters.forest, Filters.Kashyyyk_site)), 1, true));
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.Imperial), 1, true));
        modifiers.add(new UsedInterruptModifier(self, Filters.Wookiee_Strangle));
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, Filters.and(Filters.Bowcaster, Filters.not(Icon.VIRTUAL_SET_16)), 1, self));
        modifiers.add(new ResetCalculationVariableModifier(self, Filters.and(Filters.Bowcaster, Filters.attachedTo(self)), 1, Variable.X));
        modifiers.add(new DestinyModifier(self, Filters.and(Filters.your(self), Filters.bowcaster), 3));
        return modifiers;
    }
}
