package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.conditions.DoubledCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Special Modifications
 */
public class Card1_065 extends AbstractNormalEffect {
    public Card1_065() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Special_Modifications, Uniqueness.UNIQUE);
        setLore("Han's 'special modifications' for the Millennium Falcon included security mechanisms, deflector shields, hull plating, faster hyperdrive and enhanced weapons.");
        setGameText("Use 1 Force to deploy on any starship to add 2 to its armor or maneuver. If on Falcon with Han, Lando or Chewie piloting, also adds 2 to power and forfeit.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        Evaluator evaluator = new ConditionEvaluator(1, 2, new DoubledCondition(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, evaluator));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.starship;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);
        Evaluator evaluator = new ConditionEvaluator(2, 4, new DoubledCondition(self));
        Condition onFalconWithHanLandoOrChewie = new AttachedCondition(self, Filters.and(Filters.Falcon,
                Filters.hasPiloting(self, Filters.or(Filters.Han, Filters.Lando, Filters.Chewie))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ArmorModifier(self, attachedTo, new AttachedCondition(self, Filters.hasArmorDefined), evaluator));
        modifiers.add(new ManeuverModifier(self, attachedTo, new AttachedCondition(self, Filters.hasManeuverDefined), evaluator));
        modifiers.add(new PowerModifier(self, attachedTo, onFalconWithHanLandoOrChewie, evaluator));
        modifiers.add(new ForfeitModifier(self, attachedTo, onFalconWithHanLandoOrChewie, evaluator));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, attachedTo,
                new GameTextModificationCondition(self, ModifyGameTextType.SPECIAL_MODIFICATIONS__IMMUNE_TO_ATTRITION_LESS_THAN_FOUR), 4));
        return modifiers;
    }
}