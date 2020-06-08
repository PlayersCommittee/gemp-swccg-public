package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier for adding political agendas.
 */
public class AgendaModifier extends AbstractModifier {
    private Agenda[] _agendas;

    /**
     * Creates a modifier for adding political agendas.
     * @param source the card that is the source of the modifier and whom is given political agendas.
     * @param agendas the political agendas
     */
    public AgendaModifier(PhysicalCard source, Agenda... agendas) {
        super(source, null, source, null, ModifierType.GIVE_AGENDA, false);
        _agendas = agendas;
    }

    @Override
    public boolean hasAgenda(Agenda agenda) {
        for (Agenda curAgenda : _agendas) {
            if (curAgenda == agenda) {
                return true;
            }
        }
        return false;
    }
}
