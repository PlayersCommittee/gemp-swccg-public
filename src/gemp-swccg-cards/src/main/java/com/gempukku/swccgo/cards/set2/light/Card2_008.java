package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Garouf Lafoe
 */
public class Card2_008 extends AbstractAlien {
    public Card2_008() {
        super(Side.LIGHT, 3, 3, 1, 1, 2, "Garouf Lafoe", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Free-trader who imports ice chunks from rings of Ohann and Adriana, outer planets in Tatoo system. Sales remain cold...due to steep Imperial taxes and bribes.");
        setGameText("Adds 1 to power of anything he pilots. Adds 1 to forfeit of each of your characters at same Tatooine site. Subtracts 1 from forfeit of each of opponent's characters at same Hoth site. Game text suspended if at same site as a tax collector.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, self, new AtSameSiteAsCondition(self, Filters.tax_collector)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.atSameSite(self)),
                new AtCondition(self, Filters.Tatooine_site), 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.atSameSite(self)),
                new AtCondition(self, Filters.Hoth_site), -1));
        return modifiers;
    }
}
