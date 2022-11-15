package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: System
 * Title: Alderaan
 */
public class Card1_121 extends AbstractSystem {
    public Card1_121() {
        super(Side.LIGHT, Title.Alderaan, 2, ExpansionSet.PREMIERE, Rarity.U2);
        setLocationDarkSideGameText("If you control, Force drain +1 here.");
        setLocationLightSideGameText("Subtract 1 from each of opponent's battle destiny draws here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, self, -1, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}