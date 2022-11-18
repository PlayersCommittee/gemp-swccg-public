package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: System
 * Title: Naboo (V) (Dark)
 */
public class Card216_010 extends AbstractSystem {
    public Card216_010() {
        super(Side.DARK, Title.Naboo, 5, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If you control with a [Trade Federation] starship, your Force generation is +1 here.");
        setLocationLightSideGameText("If opponent controls and In Complete Control on table, your characters and vehicles deploy +1 to Naboo sites.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PLANET, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceGenerationModifier(self, new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.and(Filters.icon(Icon.TRADE_FEDERATION), Filters.starship)), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.character, Filters.vehicle)),
                new AndCondition(new ControlsCondition(game.getOpponent(playerOnLightSideOfLocation), self),new OnTableCondition(self, Filters.In_Complete_Control)),
                1, Filters.Naboo_site));
        return modifiers;
    }
}