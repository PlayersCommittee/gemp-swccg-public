package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Garon Nas Tal
 */
public class Card6_014 extends AbstractAlien {
    public Card6_014() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Garon Nas Tal", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Saurin combat expert. Hired by Jabba to train his Gamorrean guards. Regrets taking the job. Dislikes Jabba and his henchbeings. Plotting to kill Jabba.");
        setGameText("While at Audience Chamber, all your other Saurins are forfeit +2 and all your characters trained by Sai'torr Kal Fas are power and forfeit +1.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.SAURIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourCharactersTrained = Filters.and(Filters.your(self), Filters.character, Filters.trainedBy(Filters.Saitorr_Kal_Fas));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Saurin), atAudienceChamber, 2));
        modifiers.add(new PowerModifier(self, yourCharactersTrained, atAudienceChamber, 1));
        modifiers.add(new ForfeitModifier(self, yourCharactersTrained, atAudienceChamber, 1));
        return modifiers;
    }
}
