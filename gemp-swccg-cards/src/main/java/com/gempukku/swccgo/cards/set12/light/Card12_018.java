package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Murr Danod
 */
public class Card12_018 extends AbstractAlien {
    public Card12_018() {
        super(Side.LIGHT, 2, 3, 2, 3, 3, "Murr Danod", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("A peaceful Ithorian, Murr is a member of a Trade Guild based on an Ithorian herd ship. He treats his customers honestly, but does like to deal in 'grey' items. Smuggler.");
        setGameText("Your weapons and devices deploy -1 to same site. While with your unique (*) smuggler at an exterior site, opponent's total battle destiny here is -1 for each [Light Side] at same site.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.ITHORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.weapon, Filters.device)),
                -1, Filters.sameSite(self)));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new AndCondition(
                new WithCondition(self, Filters.and(Filters.your(self), Filters.unique, Filters.smuggler)),
                new AtCondition(self, Filters.exterior_site)), new NegativeEvaluator(new ForceIconsAtLocationEvaluator(self, false, true)),
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
