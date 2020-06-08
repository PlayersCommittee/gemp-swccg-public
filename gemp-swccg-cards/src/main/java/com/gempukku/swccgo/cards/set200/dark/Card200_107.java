package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Image Of The Dark Lord (V)
 */
public class Card200_107 extends AbstractNormalEffect {
    public Card200_107() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Image Of The Dark Lord", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Vader's hologram exacts loyalty from his legions.");
        setGameText("Deploy on a planet site. Opponent's Force drains are -1 here (-2 if Vader controls an adjacent site).");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.HOLOGRAM);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.planet_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new ConditionEvaluator(-1, -2,
                new ControlsWithCondition(self, playerId, Filters.adjacentSite(self), Filters.Vader)), opponent));
        return modifiers;
    }
}