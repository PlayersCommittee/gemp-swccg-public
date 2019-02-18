package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

public class SpeciesModifier extends AbstractModifier{
    private Species _species;
    private Evaluator _evaluator;

    public SpeciesModifier(PhysicalCard source, Species species) {
        this(source, source, species, 1);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Species species) {
        this(source, affectFilter, species, 1);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Species species, int count) {
        this(source, affectFilter, null, species, count);
    }

    public SpeciesModifier(PhysicalCard source, Condition condition, Species species) {
        this(source, source, condition, species, 1);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Species species) {
        this(source, affectFilter, condition, species, 1);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Species species, int count) {
        this(source, affectFilter, condition, species, new ConstantEvaluator(count));
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Species species, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.GIVE_SPECIES, true);
        _species = species;
        _evaluator = evaluator;
    }

    public Species getSpecies() {
        return _species;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _species.getHumanReadable();
    }

    @Override
    public boolean hasSpecies(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Species species) {
        return (species == _species && _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard) > 0);
    }
}
