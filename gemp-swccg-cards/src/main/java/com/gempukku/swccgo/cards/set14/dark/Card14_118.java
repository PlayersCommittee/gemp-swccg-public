package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtSameSystemAsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: DFS-327
 */
public class Card14_118 extends AbstractStarfighter {
    public Card14_118() {
        super(Side.DARK, 2, 2, 2, null, 2, null, 3, "DFS-327", Uniqueness.UNIQUE);
        setLore("Droid starfighter programmed for battleship defense. Will not pursue enemy starfighters once repelled, but can disable vital systems on attacking capital starships.");
        setGameText("While at same system as your battleship, DFS-327 is power +3, and opponent's pilots deploy +2 to starfighters at this system.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.DFS_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atSameSystemAsYourBattleship = new AtSameSystemAsCondition(self, Filters.and(Filters.your(self), Filters.battleship));
        Filter opponentsPilots = Filters.and(Filters.opponents(self), Filters.pilot);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atSameSystemAsYourBattleship, 3));
        modifiers.add(new DeployCostToTargetModifier(self, opponentsPilots, atSameSystemAsYourBattleship, 2, Filters.and(Filters.starfighter, Filters.atSameSystem(self))));
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, opponentsPilots, atSameSystemAsYourBattleship,2, Filters.starfighter, Filters.here(self)));
        return modifiers;
    }
}
