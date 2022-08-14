package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeExcludedFromBattle;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 5
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Night Club
 */
public class Card601_245 extends AbstractSite {
    public Card601_245() {
        super(Side.LIGHT, Title.Nightclub, Title.Coruscant);
        setLocationDarkSideGameText("Your characters here are defense value -1 and may not be excluded from battle.");
        setLocationLightSideGameText("Your characters here are defense value -1 and may not be excluded from battle.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourCharacterHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourCharacterHere, -1));
        modifiers.add(new MayNotBeExcludedFromBattle(self, yourCharacterHere));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourCharacterHere = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, yourCharacterHere, -1));
        modifiers.add(new MayNotBeExcludedFromBattle(self, yourCharacterHere));
        return modifiers;
    }
}