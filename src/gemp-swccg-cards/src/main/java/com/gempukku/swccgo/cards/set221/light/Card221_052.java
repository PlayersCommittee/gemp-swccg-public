package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: System
 * Title: Christophsis
 */
public class Card221_052 extends AbstractSystem {
    public Card221_052() {
        super(Side.LIGHT, Title.Christophsis, 6, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("If you control with a [Separatist] starship, opponent must use +1 Force to move or deploy a starship to here.");
        setLocationLightSideGameText("If you occupy with a [Clone Army] starship, opponent must use +1 Force to move or deploy a starship to here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLONE_ARMY, Icon.PLANET, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition condition = new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.and(Icon.SEPARATIST, Filters.starship));
        Filter opponentsStarships = Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.starship);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, opponentsStarships, condition, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, opponentsStarships, condition,1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition condition = new OccupiesWithCondition(playerOnLightSideOfLocation, self, Filters.and(Icon.CLONE_ARMY, Filters.starship));
        Filter opponentsStarships = Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.starship);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, opponentsStarships, condition, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, opponentsStarships, condition,1, self));
        return modifiers;
    }
}