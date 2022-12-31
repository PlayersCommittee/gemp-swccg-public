package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Polarized Negative Power Coupling
 */
public class Card4_031 extends AbstractNormalEffect {
    public Card4_031() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Polarized Negative Power Coupling", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Imbalance in a converter's inverse energy field can improperly polarize the negative axis, causing hyperdrive failure. Replacement often requires an inverted engineer.");
        setGameText("Deploy on table. Turn every Used Pile face up. All subsequent cards placed in Used Piles are also turned face up. (Players must commit to using cards before revealing them.) Turn used cards face down whenever they are re-circulated.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.USED_PILES_FACE_UP));
        return modifiers;
    }
}