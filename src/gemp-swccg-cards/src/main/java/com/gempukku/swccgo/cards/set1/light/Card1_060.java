package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Rebel Planners
 */
public class Card1_060 extends AbstractNormalEffect {
    public Card1_060() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Rebel_Planners, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Rebel strategists worked under master tactician General Dodonna. They devised an unorthodox battle plan to destroy the Death Star at the Battle of Yavin.");
        setGameText("Use 1 Force to deploy at Massassi War Room or any docking bay. Adds X to total power of your starships at the related system and related sectors, where X = the number of your starships present.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Massassi_War_Room, Filters.docking_bay);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition gameTextModified = new GameTextModificationCondition(self, ModifyGameTextType.REBEL_PLANNERS__APPLIES_TO_EVERY_SYSTEM);
        final int permCardId = self.getPermanentCardId();
        Evaluator evaluator = new CalculateCardVariableEvaluator(self, Variable.X) {
            @Override
            protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.starship, Filters.present(cardAffected)));
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.relatedSystemOrSector(self), new NotCondition(gameTextModified), evaluator, playerId));
        modifiers.add(new TotalPowerModifier(self, Filters.or(Filters.system, Filters.relatedSector(self)), gameTextModified, evaluator, playerId));
        return modifiers;
    }
}