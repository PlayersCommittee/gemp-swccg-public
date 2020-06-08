package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.EffectsOfRevolutionCanceledCondition;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RotateLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Revolution
 */
public class Card1_062 extends AbstractNormalEffect {
    public Card1_062() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Revolution);
        setLore("Spies and informants gave Rebels vital data to plan strategic turnabouts, create local insurgencies, and hamper Imperial activities.");
        setGameText("Use 3 Force to deploy on any location. Rotate the location so that icons and game texts switch direction. (If Expand the Empire present, it also switches.) If Revolution later canceled, location rotates back again.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new RotateLocationModifier(self, Filters.hasAttached(self), new NotCondition(new EffectsOfRevolutionCanceledCondition(self))));
        return modifiers;
    }

    // Note: EffectsOfRevolutionRule handles the actual rotating of the location.
}