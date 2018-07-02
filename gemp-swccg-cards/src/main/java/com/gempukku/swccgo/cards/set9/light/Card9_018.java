package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Karie Neth
 */
public class Card9_018 extends AbstractRebel {
    public Card9_018() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Karie_Neth, Uniqueness.UNIQUE);
        setLore("Gray Squadron 2's gunner at the Battle of Endor. Member of Rogue Squadron. Replaced Bothan pilot lost during secret mission ordered by Rebel command staff.");
        setGameText("Adds 2 to power of anything she pilots. While aboard your starship, adds 1 to each of its weapon destiny draws (2 if Gray Squadron 2 or when with Telsij).");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GRAY_SQUADRON, Keyword.GUNNER, Keyword.ROGUE_SQUADRON, Keyword.FEMALE);
        setMatchingStarshipFilter(Filters.Gray_Squadron_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardYourStarship = new AboardCondition(self, Filters.and(Filters.your(self), Filters.starship));
        Condition aboardGraySquadron2OrWithTelsij = new OrCondition(new AboardCondition(self, Filters.Gray_Squadron_2), new WithCondition(self, Filters.Telsij));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, new AndCondition(aboardYourStarship, new NotCondition(aboardGraySquadron2OrWithTelsij)), Filters.hasAboard(self), 1, Filters.any));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, new AndCondition(aboardYourStarship, aboardGraySquadron2OrWithTelsij), Filters.hasAboard(self), 2, Filters.any));
        return modifiers;
    }
}
