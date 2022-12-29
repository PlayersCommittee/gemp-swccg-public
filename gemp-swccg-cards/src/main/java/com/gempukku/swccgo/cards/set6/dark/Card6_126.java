package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Trandoshan
 */
public class Card6_126 extends AbstractAlien {
    public Card6_126() {
        super(Side.DARK, 3, 3, 3, 1, 2, "Trandoshan", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Trandoshans refer to themselves as T'doshok. Hate Wookiees. Many have come to work for Jabba, attempting to emulate the success of Bossk.");
        setGameText("Power and Forfeit +1 at same site as a Wookiee or while Bossk at Audience Chamber. When in battle at same site as a bounty, adds 1 to attrition against opponent.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.TRANDOSHAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atSameSiteAsWookieeOrWhileBosskAtAudienceChamber = new OrCondition(new AtSameSiteAsCondition(self, Filters.Wookiee),
                new AtCondition(self, Filters.Bossk, Filters.Audience_Chamber));


        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atSameSiteAsWookieeOrWhileBosskAtAudienceChamber, 1));
        modifiers.add(new ForfeitModifier(self, atSameSiteAsWookieeOrWhileBosskAtAudienceChamber, 1));
        modifiers.add(new AttritionModifier(self, new InBattleAtCondition(self, Filters.sameSiteAs(self, Filters.any_bounty)),
                1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
