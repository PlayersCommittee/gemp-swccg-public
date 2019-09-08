package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtLeastNumberOfSpeciesOnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10
 * Type: Effect
 * Title: Ancient Watering Hole
 */
public class Card210_007 extends AbstractNormalEffect {
    public Card210_007() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ancient Watering Hole", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on table. Maz and your Rep are immune to attrition. While you have alien characters of five different species on table: your Force drains are +1, your total battle destiny is +1 (+2 if Maz or your Rep in battle), and your aliens are forfeit +1. [Immune to Alter]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_10, Icon.EPISODE_VII);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final PhysicalCard rep = game.getGameState().getRep(playerId);
        Filter repFilter = Filters.none;
        if (rep != null) {
            repFilter = Filters.sameTitle(rep);
        }
        Filter mazOrYourRepFilter = Filters.or(Filters.Maz, repFilter);

        Condition fiveDifferentSpeciesCondition = new AtLeastNumberOfSpeciesOnTableCondition(game, self, 5);
        Condition mazOrRepInBattle = new InBattleCondition(self, mazOrYourRepFilter);

        modifiers.add(new ImmuneToAttritionModifier(self, mazOrYourRepFilter));
        modifiers.add(new ForceDrainModifier(self, Filters.any, fiveDifferentSpeciesCondition, 1, playerId));
        modifiers.add(new TotalBattleDestinyModifier(self, new AndCondition(fiveDifferentSpeciesCondition, new NotCondition(mazOrRepInBattle)), 1, playerId));
        modifiers.add(new TotalBattleDestinyModifier(self, new AndCondition(fiveDifferentSpeciesCondition, mazOrRepInBattle), 2, playerId));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerId), Filters.alien), fiveDifferentSpeciesCondition, 1));

        return modifiers;
    }
}