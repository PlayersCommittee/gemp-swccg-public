package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Gunner
 */
public class Card3_088 extends AbstractImperial {
    public Card3_088() {
        super(Side.DARK, 1, 1, 1, 1, 1, "Imperial Gunner");
        setLore("Walker operators work in pairs, one trained to pilot the AT-AT, the other serving as gunner. Imperial gunners consider themselves the best marksmen in the Empire.");
        setGameText("Adds 1 to weapon destiny draws of anything he is aboard as a passenger.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.GUNNER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.hasPassenger(self), 1));
        return modifiers;
    }
}
