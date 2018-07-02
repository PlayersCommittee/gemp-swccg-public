package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsePoliticsForPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Galactic Senate
 */
public class Card12_167 extends AbstractSite {
    public Card12_167() {
        super(Side.DARK, Title.Galactic_Senate, Title.Coruscant);
        setLocationDarkSideGameText("During battles here, a character's power is equal to that character's politics.");
        setLocationLightSideGameText("During battles here, a character's power is equal to that character's politics.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsePoliticsForPowerModifier(self, Filters.and(Filters.character, Filters.here(self)), new DuringBattleAtCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsePoliticsForPowerModifier(self, Filters.and(Filters.character, Filters.here(self)), new DuringBattleAtCondition(self)));
        return modifiers;
    }
}