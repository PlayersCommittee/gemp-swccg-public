package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Pilot
 */
public class Card1_180 extends AbstractImperial {
    public Card1_180() {
        super(Side.DARK, 3, 2, 0, 2, 2, "Imperial Pilot");
        setLore("Among the Empire's best pilots. Loyal and fearless. Use superior numbers to overwhelm opponents. Trained under combat conditions. Wear sealed, high-gravity flight suits.");
        setGameText("Adds 2 to power of anything he pilots.");
        addIcons(Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }
}
