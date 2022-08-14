package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerStarDestroyerEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Kuat
 */
public class Card7_286 extends AbstractSystem {
    public Card7_286() {
        super(Side.DARK, Title.Kuat, 1);
        setLocationDarkSideGameText("Your starships move as a 'react' (for free) to a battle here.");
        setLocationLightSideGameText("If you occupy, all Star Destroyers are deploy +1 (+3 if you control). Your movement to here requires +1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationForFreeModifier(self, "Move starship as a react", playerOnDarkSideOfLocation, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship), Filters.and(self, Filters.battleLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.Star_Destroyer, new OccupiesCondition(playerOnLightSideOfLocation, self),
                new ConditionEvaluator(new PerStarDestroyerEvaluator(1), new PerStarDestroyerEvaluator(3), new ControlsCondition(playerOnLightSideOfLocation, self))));
        modifiers.add(new MoveCostToLocationModifier(self, Filters.your(playerOnLightSideOfLocation), 1, self));
        return modifiers;
    }
}