package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: AT-AT Driver
 */
public class Card3_083 extends AbstractImperial {
    public Card3_083() {
        super(Side.DARK, 1, 2, 1, 1, 2, "AT-AT Driver");
        setLore("Piloting walkers high above the battlefield, AT-AT drivers are protected by 15 centimeters of reinforced armor. Accordingly, they are regarded with contempt by the infantry.");
        setGameText("Adds 2 to power of any combat vehicle he pilots (3 if combat vehicle is an AT-AT).");
        addIcons(Icon.HOTH, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.AT_AT), Filters.combat_vehicle));
        return modifiers;
    }
}
