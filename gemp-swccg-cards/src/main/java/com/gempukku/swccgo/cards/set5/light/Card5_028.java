package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Leia Of Alderaan
 */
public class Card5_028 extends AbstractNormalEffect {
    public Card5_028() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Leia_Of_Alderaan, Uniqueness.UNIQUE);
        setLore("The face that launched a thousand starships.");
        setGameText("Deploy on Leia. While at any Rebel Base site, your Rebels, except unique (•) Rebels, are deploy -2 and power +2 at same and adjacent sites. While at any system, your starfighters, except unique (•) starfighters, are deploy -2 there.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Leia;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Leia;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAnyRebelBaseSite = new AtCondition(self, Filters.Rebel_Base_site);
        Filter yourNonuniqueRebels = Filters.and(Filters.your(self), Filters.non_unique, Filters.Rebel);
        Condition atAnySystem = new AtCondition(self, Filters.system);
        Filter yourNonuniqueStarfighters = Filters.and(Filters.your(self), Filters.non_unique, Filters.starfighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourNonuniqueRebels, atAnyRebelBaseSite, -2, Filters.sameOrAdjacentSite(self)));
        modifiers.add(new PowerModifier(self, Filters.and(yourNonuniqueRebels, Filters.atSameOrAdjacentSite(self)), atAnyRebelBaseSite, 2));
        modifiers.add(new DeployCostToLocationModifier(self, yourNonuniqueStarfighters, atAnySystem, -2, Filters.here(self)));
        return modifiers;
    }
}