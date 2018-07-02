package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Sei Taria
 */
public class Card12_027 extends AbstractRepublic {
    public Card12_027() {
        super(Side.LIGHT, 3, 3, 2, 2, 5, Title.Sei_Taria, Uniqueness.UNIQUE);
        setPolitics(3);
        setLore("Valorum's female administrative aide who has studied the taxation bills. After Valorum was voted out of his position as Chancellor, Sei chose to leave political life.");
        setGameText("Agendas: peace, taxation. If with Valorum, your Force generation at this location is +1. While in a senate majority, your Force drains are +1 at battleground systems while you control a battleground site.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.PEACE, Agenda.TAXATION));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.sameLocation(self), new WithCondition(self, Filters.Valorum), 1, playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.battleground_system, new AndCondition(new InSenateMajorityCondition(self),
                new ControlsCondition(playerId, Filters.battleground_site)), 1, playerId));
        return modifiers;
    }
}
