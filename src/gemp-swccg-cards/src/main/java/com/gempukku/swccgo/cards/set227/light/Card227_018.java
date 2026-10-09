package com.gempukku.swccgo.cards.set227.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.OutOfPlayCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

/**
 * Set: Set 27
 * Type: Starship
 * Subtype: Starfighter
 * Title: Beckett's Stolen AT-Hauler
 */
public class Card227_018 extends AbstractStarfighter {
    public Card227_018() {
        super(Side.LIGHT, 2, 4, 4, 4, null, 4, 5, "Beckett's Stolen AT-Hauler", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("");
        setGameText("May add 2 pilots and 6 passengers. Permanent pilot provides ability of 2. Players may not draw more than two battle destiny (one if piloted by Rio) here. While Beckett on table or out of play, power +3.");
        addIcons(Icon.SCOMP_LINK, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_27);
        addModelType(ModelType.Y_45_ARMORED_TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(6);
        setAlwaysStolen(true);
        setMatchingPilotFilter(Filters.Beckett);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new ConditionEvaluator(2, 1, new HasPilotingCondition(self, Filters.Rio)), playerId));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new ConditionEvaluator(2, 1, new HasPilotingCondition(self, Filters.Rio)), opponent));
        Condition beckettOnTableOrOutOfPlay = new OrCondition(new OnTableCondition(self, Filters.Beckett), new OutOfPlayCondition(self, Filters.Beckett));
        modifiers.add(new PowerModifier(self, beckettOnTableOrOutOfPlay, 3));
        return modifiers;
    }
}
