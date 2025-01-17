package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 24
 * Type: Starship
 * Subtype: Capital
 * Title: Liberator
 */

public class Card224_019 extends AbstractCapitalStarship {
    public Card224_019() {
        super(Side.LIGHT, 2, 4, 5, 4, null, 3, 7, "Liberator", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Phoenix Squadron.");
        setGameText("May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Phoenix Squadron pilots deploy -1 aboard. While at opponent's battleground system, Force drains may not be modified or canceled here.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_24);
        addIcon(Icon.PILOT, 1);
        addKeywords(Keyword.PHOENIX_SQUADRON);
        addModelType(ModelType.CORELLIAN_CORVETTE);
        setPilotCapacity(3);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    public final Filter phoenixSquadPilots = Filters.and(Filters.Phoenix_Squadron_character, Filters.pilot);
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, phoenixSquadPilots, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, phoenixSquadPilots, -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition atOpponentsBattlegroundSystem = new AtCondition(self, Filters.and(Filters.opponents(playerId), Filters.battleground_system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, Filters.here(self), atOpponentsBattlegroundSystem, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), atOpponentsBattlegroundSystem, opponent, playerId));
        return modifiers;
    }
}
