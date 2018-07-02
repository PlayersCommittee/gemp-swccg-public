package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PodraceForceLossModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: You've Never Won A Race?
 */
public class Card13_100 extends AbstractDefensiveShield {
    public Card13_100() {
        super(Side.DARK, "You've Never Won A Race?");
        setLore("'Not even finished?'");
        setGameText("Plays on table. Unless opponent occupies three battlegrounds, I Did It! is suspended. If Sebulba's Podracer on table, your Force loss from Boonta Eve Podrace is reduced by X, where X = number of battlegrounds you occupy.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.I_Did_It, new UnlessCondition(new OccupiesCondition(opponent, 3, Filters.battleground))));
        modifiers.add(new PodraceForceLossModifier(self, new OnTableCondition(self, Filters.Sebulbas_Podracer), new NegativeEvaluator(new OccupiesEvaluator(playerId, Filters.battleground)), playerId));
        return modifiers;
    }
}