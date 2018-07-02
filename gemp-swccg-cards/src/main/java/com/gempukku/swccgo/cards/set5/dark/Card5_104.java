package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Ugloste
 */
public class Card5_104 extends AbstractAlien {
    public Card5_104() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Ugloste", Uniqueness.UNIQUE);
        setLore("Ugnaught assigned to determine how to use carbon-freezing on humans. Placed in charge or the Ugnaught workers on Cloud City. Formerly enslaved by humans.");
        setGameText("Power +2 at Trash Compactor, Droid Junkheap, Incinerator or Carbonite Chamber. Functions as a leader if present with another Ugnaught. Where present, other Ugnaughts are forfeit +2 and double their bonus to Carbon-Freezing destiny.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        setSpecies(Species.UGNAUGHT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter otherUgnaughtsWherePresent = Filters.and(Filters.other(self), Filters.Ugnaught, Filters.at(Filters.wherePresent(self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Trash_Compactor, Filters.Droid_Junkheap,
                Filters.Incinerator, Filters.Carbonite_Chamber)), 2));
        modifiers.add(new LeaderModifier(self, new PresentWithCondition(self, Filters.and(Filters.other(self), Filters.Ugnaught))));
        modifiers.add(new ForfeitModifier(self, otherUgnaughtsWherePresent, 2));
        modifiers.add(new ModifyGameTextModifier(self, otherUgnaughtsWherePresent, ModifyGameTextType.UGNAUGHT__DOUBLE_CARBON_FREEZING_DESTINY_BONUS));
        return modifiers;
    }
}
