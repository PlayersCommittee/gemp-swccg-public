package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToDrivenBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Pote Snitkin
 */
public class Card6_119 extends AbstractAlien {
    public Card6_119() {
        super(Side.DARK, 3, 3, 2, 2, 3, "Pote Snitkin", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Skrilling smuggler. Supplied Jabba's henchmen with weapons when he was Hermi Odle's predecessor. An excellent driver.");
        setGameText("When driving a vehicle, that vehicle is power +3 and moves for free. While at Audience Chamber, all your other Skrillings are power +2 and forfeit +1.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.SKRILLING);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherSkrillings = Filters.and(Filters.your(self), Filters.other(self), Filters.Skrilling);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 3));
        modifiers.add(new MovesForFreeModifier(self, Filters.hasDriving(self)));
        modifiers.add(new PowerModifier(self, yourOtherSkrillings, atAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, yourOtherSkrillings, atAudienceChamber, 1));
        return modifiers;
    }
}
