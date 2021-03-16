package com.gempukku.swccgo.cards.set601.light;

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
 * Title: Affect Mind (V)
 */
public class Card601_064 extends AbstractDefensiveShield {
    public Card601_064() {
        super(Side.LIGHT, "Affect Mind");
        setVirtualSuffix(true);
        setLore("'What was that?' The Jedi power known as 'affect mind' is often used to create minor distractions, allowing Jedi to elude enemies rather than engage them in battle.");
        setGameText("Plays on table. Unless Inner Strength on table, opponent may use only one combat card per turn. While opponent has 2 Dark Jedi on Naboo, you lose no more than 2 Force to Force drains at opponent's Naboo sites. Let Them Make The First Move may target only droids and spies.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_D);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.opponents(playerId), Filters.Naboo_site), new OnTableCondition(self, 2, Filters.and(Filters.Dark_Jedi, Filters.on(Title.Naboo))), 2, playerId));
        modifiers.add(new SpecialFlagModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Inner_Strength)), ModifierFlag.MAY_ONLY_USE_ONE_COMBAT_CARD_PER_TURN, opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Let_Them_Make_The_First_Move, ModifyGameTextType.LEGACY__LET_THEM_MAKE_THE_FIRST_MOVE__ONLY_TARGET_DROIDS_AND_SPIES));
        return modifiers;
    }
}