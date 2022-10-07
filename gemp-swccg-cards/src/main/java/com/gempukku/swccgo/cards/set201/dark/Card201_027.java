package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Alien
 * Title: Wooof (V)
 */
public class Card201_027 extends AbstractAlien {
    public Card201_027() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Wooof", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Kadas'sa'Nikto. One of Jabba's best pilots. Often pilots Jabba's space yacht. Smuggler. Prefers to be flying combat starfighters.");
        setGameText("[Pilot] 3. May deploy with any cruiser using Combat Response. While aboard a piloted [Independent] starship, opponent's battle and weapon destiny draws here are -1.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition aboardPilotedIndependentStarship = new AboardCondition(self, Filters.and(Filters.starship, Filters.piloted, Icon.INDEPENDENT));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), aboardPilotedIndependentStarship, -1, opponent));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.here(self)), aboardPilotedIndependentStarship, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier(self, Filters.cruiser));
        return modifiers;
    }
}
