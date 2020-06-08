package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMakeKesselRunInsteadOfSmugglerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: BoShek
 */
public class Card1_004 extends AbstractAlien {
    public Card1_004() {
        super(Side.LIGHT, 1, 4, 2, 4, 3, "BoShek", Uniqueness.UNIQUE);
        setLore("Rogue pilot. Outlaw starship tech. Has secret lab in Mos Eisley. He bragged about beating Han Solo's Kessel Run record. Left fringe life behind after meeting Obi-Wan Kenobi.");
        setGameText("Adds 3 to power of anything he pilots. May make a Kessel Run in place of a smuggler. Immune to attrition < 3.");
        addIcons(Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayMakeKesselRunInsteadOfSmugglerModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
