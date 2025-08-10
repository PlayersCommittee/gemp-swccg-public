package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtLeastNumberOfAlienSpeciesOnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Effect
 * Title: Ancient Watering Hole
 */
public class Card210_007 extends AbstractNormalEffect {
    public Card210_007() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ancient Watering Hole", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("");
        setGameText("Deploy on table. Your Rep is immune to attrition. While you have alien characters of five different species on table, at locations where you have an alien: Your total battle destiny is +1, your aliens are forfeit +1 and, if location is a battleground, your Force drains are +1. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_10, Icon.EPISODE_VII);
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
        
        Filter yourAliens = Filters.and(Filters.your(playerId), Filters.alien);
        Filter yourAliensAtLocations = Filters.and(yourAliens, Filters.at(Filters.any));
        Filter locationsWithYourAlien = Filters.sameLocationAs(self, yourAliens);
        Filter battlegroundsWithYourAlien = Filters.and(Filters.battleground, locationsWithYourAlien);

        Condition fiveDifferentSpeciesCondition = new AtLeastNumberOfAlienSpeciesOnTableCondition(game, self, 5);

        modifiers.add(new ImmuneToAttritionModifier(self, repFilter));
        modifiers.add(new TotalBattleDestinyModifier(self, locationsWithYourAlien, fiveDifferentSpeciesCondition, 1, playerId));
        modifiers.add(new ForfeitModifier(self, yourAliensAtLocations, fiveDifferentSpeciesCondition, 1));
        modifiers.add(new ForceDrainModifier(self, battlegroundsWithYourAlien, fiveDifferentSpeciesCondition, 1, playerId));

        return modifiers;
    }
}