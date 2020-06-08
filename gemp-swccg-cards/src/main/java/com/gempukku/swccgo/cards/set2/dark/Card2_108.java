package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: URoRRuR'R'R
 */
public class Card2_108 extends AbstractAlien {
    public Card2_108() {
        super(Side.DARK, 3, 3, 1, 1, 4, Title.URoRRuRRR, Uniqueness.UNIQUE);
        setLore("Leader of a Tusken Raider tribe. Unafraid of machines. Skilled hunter and marksman. Raids moisture farms for water. Roams the Jundland Wastes in search of unwary travelers.");
        setGameText("Deploys only on on Tatooine. When at same site as another Tusken Raider, may draw one battle destiny if not able to otherwise.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.TUSKEN_RAIDER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AtSameSiteAsCondition(self, Filters.and(Filters.other(self), Filters.Tusken_Raider)), 1));
        return modifiers;
    }
}
