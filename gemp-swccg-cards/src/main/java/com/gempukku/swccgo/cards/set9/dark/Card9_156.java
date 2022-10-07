package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Emperor's Personal Shuttle
 */
public class Card9_156 extends AbstractStarfighter {
    public Card9_156() {
        super(Side.DARK, 3, 3, 2, null, 3, 3, 5, "Emperor's Personal Shuttle", Uniqueness.UNIQUE);
        setLore("Transport used extensively by Emperor Palpatine. Extremely well guarded. Pilots ordered to maintain constant contact with all nearby Imperial strike forces.");
        setGameText("May add 1 pilot and 4 passengers. Permanent pilot provides ability 2. Emperor deploys for free aboard. While Emperor aboard, immune to attrition and adds one battle destiny.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.LAMBDA_CLASS_SHUTTLE);
        setPilotCapacity(1);
        setPassengerCapacity(4);
        setMatchingPilotFilter(Filters.Emperor);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployForFreeForSimultaneouslyDeployingPilotModifier(self, Filters.Emperor));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Emperor, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition emperorAboard = new HasAboardCondition(self, Filters.Emperor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, emperorAboard));
        modifiers.add(new AddsBattleDestinyModifier(self, emperorAboard, 1));
        return modifiers;
    }
}
