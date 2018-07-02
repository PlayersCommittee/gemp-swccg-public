package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
 * Subtype: Rebel
 * Title: Rebel Gunner
 */
public class Card3_017 extends AbstractRebel {
    public Card3_017() {
        super(Side.LIGHT, 1, 1, 1, 1, 1, "Rebel Gunner");
        setLore("Tailgunners such as Kesin Ommis from Coruscant are posted to aft gunnery stations on vehicles and starships. Highly trained in weapons operations.");
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
