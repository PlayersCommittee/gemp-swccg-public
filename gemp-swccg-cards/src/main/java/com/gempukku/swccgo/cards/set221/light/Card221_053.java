package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Christophsis: Chaleydonia
 */
public class Card221_053 extends AbstractSite {
    public Card221_053() {
        super(Side.LIGHT, "Christophsis: Chaleydonia", Title.Christophsis, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Force drain -1 here. Ventress is power +2 here.");
        setLocationLightSideGameText("Your total power here is +1 for each of your piloted starships at Christophsis system.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnDarkSideOfLocation));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Ventress, Filters.here(self)), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalPowerModifier(self, self, new OnTableEvaluator(self, Filters.and(Filters.at(Filters.Christophsis_system), Filters.your(playerOnLightSideOfLocation), Filters.piloted, Filters.starship)), playerOnLightSideOfLocation));
        return modifiers;
    }
}