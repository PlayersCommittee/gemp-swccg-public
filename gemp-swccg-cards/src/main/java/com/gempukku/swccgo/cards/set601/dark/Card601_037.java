package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block D
 * Type: Defensive Shield
 * Title: I Find Your Lack Of Faith Disturbing (V)
 */
public class Card601_037 extends AbstractDefensiveShield {
    public Card601_037() {
        super(Side.DARK, "I Find Your Lack Of Faith Disturbing");
        setVirtualSuffix(true);
        setLore("Darth Vader ruthlessly used the Force to strike down enemies and soldiers who displeased him. He could choke victims from afar without touching them.");
        setGameText("Plays on table. Unless Deep Hatred on table, opponent may use only one combat card per turn. While opponent has two Jedi on Naboo, you lose no more than 2 Force to Force drains at opponent's Naboo sites. We'll Handle This may target only droids and spies.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_D);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.opponents(playerId), Filters.Naboo_site), new OnTableCondition(self, 2, Filters.and(Filters.Jedi, Filters.on(Title.Naboo))), 2, playerId));
        modifiers.add(new SpecialFlagModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Deep_Hatred)), ModifierFlag.MAY_ONLY_USE_ONE_COMBAT_CARD_PER_TURN, opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Well_Handle_This, ModifyGameTextType.LEGACY__WELL_HANDLE_THIS__ONLY_TARGET_DROIDS_AND_SPIES));
        return modifiers;
    }
}