package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Toht Ra
 */
public class Card501_049 extends AbstractAlien {
    public Card501_049() {
        super(Side.DARK, 3, 3, 2, 2, 4, "Toht Ra", Uniqueness.UNIQUE);
        setLore("Crimson Dawn. Hylobon guard.");
        setGameText("While with a Crimson Dawn leader or at opponent’s site, opponent must use +1 force to deploy a weapon here (even a [PW]). Power and forfeit +1 if at opponent’s site (or if with another character with 'guard' in lore).");
        addKeyword(Keyword.CRIMSON_DAWN);
        setSpecies(Species.HYLOBON);
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("Toht Ra");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        Condition atOpponentsSiteCondition = new AtCondition(self, Filters.and(Filters.your(opponent), Filters.site));
        Condition withCrimsonDawnLeaderCondition = new WithCondition(self, Filters.and(Filters.leader, Filters.Crimson_Dawn));
        Condition withGuardCondition = new WithCondition(self, Filters.and(Filters.character, Filters.loreContains("guard")));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.weapon_or_character_with_permanent_weapon), new OrCondition(withCrimsonDawnLeaderCondition, atOpponentsSiteCondition), new ConstantEvaluator(1), Filters.here(self)));
        modifiers.add(new PowerModifier(self, new OrCondition(atOpponentsSiteCondition, withGuardCondition), 1));
        modifiers.add(new ForfeitModifier(self, new OrCondition(atOpponentsSiteCondition, withGuardCondition), 1));
        return modifiers;
    }
}
