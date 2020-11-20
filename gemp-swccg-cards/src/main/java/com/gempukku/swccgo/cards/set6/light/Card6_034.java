package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.TrainedByCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NotLostIfAsteroidSectorDrawnForAsteroidDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Rayc Ryjerd
 */
public class Card6_034 extends AbstractAlien {
    public Card6_034() {
        super(Side.LIGHT, 2, 3, 2, 2, 4, "Rayc Ryjerd", Uniqueness.UNIQUE);
        setLore("An 'honest' smuggler. Working for Jabba to pay off debts for his ship. Rycar's son. Even more of an idiot.");
        setGameText("Adds 2 to power of anything he pilots (or 4 if trained by Rycar Ryjerd). Any starfighter Rayc pilots is immune to Tallon Roll and is not lost if an asteroid sector is drawn for asteroid destiny.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER);
        addPersona(Persona.RAYC);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starfighterPiloted = Filters.and(Filters.starfighter, Filters.hasPiloting(self, Filters.Rayc));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 4, new TrainedByCondition(self, Filters.Rycar_Ryjerd))));
        modifiers.add(new ImmuneToTitleModifier(self, starfighterPiloted, Title.Tallon_Roll));
        modifiers.add(new NotLostIfAsteroidSectorDrawnForAsteroidDestinyModifier(self, starfighterPiloted));
        return modifiers;
    }
}
