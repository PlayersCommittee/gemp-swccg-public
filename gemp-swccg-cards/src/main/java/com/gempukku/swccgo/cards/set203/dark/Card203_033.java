package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.AfterPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Location
 * Subtype: System
 * Title: Tatooine (V)
 */
public class Card203_033 extends AbstractSystem {
    public Card203_033() {
        super(Side.DARK, Title.Tatooine, 7);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Opponent may not initiate battle here until after your first turn.");
        setLocationLightSideGameText("If a player controls, for each of their starships here, their total power is +1 in battles at Tatooine sites.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, self, new NotCondition(new AfterPlayersTurnNumberCondition(playerOnDarkSideOfLocation, 1)),
                game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String playerOnDarkSideOfLocation = game.getOpponent(playerOnLightSideOfLocation);
        Filter tatooineBattleSite = Filters.and(Filters.Tatooine_site, Filters.battleLocation);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, tatooineBattleSite, new ControlsCondition(playerOnDarkSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship)),
                playerOnDarkSideOfLocation));
        modifiers.add(new TotalPowerModifier(self, tatooineBattleSite, new ControlsCondition(playerOnLightSideOfLocation, self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship)),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}