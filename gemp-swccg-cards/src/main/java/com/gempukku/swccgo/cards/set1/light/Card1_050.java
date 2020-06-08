package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Eyes In The Dark
 */
public class Card1_050 extends AbstractNormalEffect {
    public Card1_050() {
        super(Side.LIGHT, 5, PlayCardZoneOption.NEXT_TO_EITHER_LOST_PILE, Title.Eyes_In_The_Dark);
        setLore("'I don't like the look of this.'");
        setGameText("Deploy beside either player's Lost Pile. That pile is turned face down. Cards from that player's Life Force may not be viewed when they are lost.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String zoneOwner = self.getZoneOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.LOST_PILE_FACE_DOWN, zoneOwner));
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.LIFE_FORCE_HIDDEN_WHEN_LOST, zoneOwner));
        return modifiers;
    }
}