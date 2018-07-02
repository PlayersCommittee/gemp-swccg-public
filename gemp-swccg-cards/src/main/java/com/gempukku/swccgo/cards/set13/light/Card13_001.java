package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.evaluators.DivideEvaluator;
import com.gempukku.swccgo.cards.evaluators.LosingRaceTotalEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.cards.evaluators.WinningRaceTotalEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: A Close Race
 */
public class Card13_001 extends AbstractDefensiveShield {
    public Card13_001() {
        super(Side.LIGHT, "A Close Race");
        setLore("'Poodoo!'");
        setGameText("Plays on table. If you just lost a Podrace, your Force loss is limited to half the difference between the winning race total and your highest race total (round up). While you occupy three battlegrounds, Watto's Box is suspended.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Wattos_Box, new OccupiesCondition(playerId, 3, Filters.battleground)));
        modifiers.add(new LimitForceLossFromCardModifier(self, Filters.Boonta_Eve_Podrace,
                new DivideEvaluator(new SubtractEvaluator(new WinningRaceTotalEvaluator(), new LosingRaceTotalEvaluator()), 2, true), playerId));
        return modifiers;
    }
}