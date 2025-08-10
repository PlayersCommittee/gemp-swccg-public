package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class SpeciesModifier extends AbstractModifier{
    private Species _species;
    private Evaluator _evaluator;
    private boolean _matchRepSpecies;

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

    public SpeciesModifier(PhysicalCard source, boolean matchRepSpecies) {
        this(source, source, null, null, null, matchRepSpecies);
    }

    public SpeciesModifier(PhysicalCard source, Condition condition, boolean matchRepSpecies) {
        this(source, source, condition, null, null, matchRepSpecies);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Species species, Evaluator evaluator) {
        this(source, affectFilter, condition, species, evaluator, false);
    }

    public SpeciesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Species species, Evaluator evaluator, boolean matchRepSpecies) {
        super(source, null, affectFilter, condition, ModifierType.GIVE_SPECIES, true);
        _species = species;
        _evaluator = evaluator;
        _matchRepSpecies = matchRepSpecies;
    }

    public Species getSpecies() {
        return _species;
    }

    public boolean matchRepSpecies() {
        return _matchRepSpecies;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_species == null)
            return null;
        return _species.getHumanReadable();
    }

    @Override
    public boolean hasSpecies(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Species species) {
        if (_matchRepSpecies) {
            return gameState.getRep(physicalCard.getOwner())!=null && gameState.getRep(physicalCard.getOwner()).getBlueprint().getSpecies() == species;
        }
        return (species == _species && _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard) > 0);
    }
}
