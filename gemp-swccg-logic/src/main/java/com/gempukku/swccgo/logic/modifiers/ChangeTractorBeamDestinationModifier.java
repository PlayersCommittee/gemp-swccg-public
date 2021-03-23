package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that changes which card to attach captured starships to when captured by a tractor beam
 */
public class ChangeTractorBeamDestinationModifier extends AbstractModifier {
    private Filterable _destination;

    /**
     * Creates a modifier that changes which card to attach captured starships to when captured by a tractor beam
     * @param source the source of the modifier
     * @param tractorBeam the filter for affected cards
     * @param destination the new destination for captured starships
     */
    public ChangeTractorBeamDestinationModifier(PhysicalCard source, Filterable tractorBeam, Filterable destination) {
        super(source, "Starships captured by tractor beam are placed elsewhere", tractorBeam, ModifierType.TRACTOR_BEAM_DESTINATION, true);
        _destination = destination;
    }

    public Filterable getDestination() {
        return _destination;
    }
}
