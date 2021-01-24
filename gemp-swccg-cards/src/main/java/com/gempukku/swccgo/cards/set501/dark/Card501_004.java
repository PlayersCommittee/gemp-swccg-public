package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DidNotDeployObjectiveCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 14
 * Type: Character
 * SubType: Imperial
 * Title: Admiral Piett (V)
 */
public class Card501_004 extends AbstractImperial {
    public Card501_004() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Admiral Piett", Uniqueness.UNIQUE);
        setLore("Veteran of the Imperial military machine. Leader of the Imperial fleet at Endor. Skilled at political maneuvering and appeasing his powerful superiors.");
        setGameText("While piloting Executor, adds 3 to power. Deploys -1 for each of your starship sites on table. If piloting Executor and you have no objective, Flagship Operations may deploy on Executor regardless of deployment restrictions.");
        addPersona(Persona.PIETT);
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_14);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Executor);
        setVirtualSuffix(true);
        setTestingText("Admiral Piett (V)");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourStarshipSitesFilter = Filters.and(Filters.your(self.getOwner()), Filters.starship_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, yourStarshipSitesFilter),
                new NegativeEvaluator(new OnTableEvaluator(self, yourStarshipSitesFilter))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.Executor));

        Condition condition = new AndCondition(new PilotingCondition(self, Filters.Executor),
                                            new DidNotDeployObjectiveCondition(self.getOwner())
        );

        modifiers.add(new ModifyGameTextModifier(self, Filters.Flagship_Operations, condition, ModifyGameTextType.FLAGSHIP_OPERATIONS__MAY_DEPLOY_ON_EXECUTOR));
        return modifiers;
    }
}
