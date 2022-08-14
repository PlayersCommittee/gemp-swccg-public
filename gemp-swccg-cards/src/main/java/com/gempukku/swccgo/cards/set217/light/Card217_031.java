package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien
 * Title: Boba
 */
public class Card217_031 extends AbstractAlien {
    public Card217_031() {
        super(Side.LIGHT, 1, 4, 4, 3, 6, "Boba", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Mandalorian.");
        setGameText("[Pilot] 3. Deploys free to opponent's Audience Chamber. While opponent has more characters here than you, adds one battle destiny. Opponent's characters of lesser ability are power -1 here. Immune to Hidden Weapons.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
        setSpecies(Species.MANDALORIAN);
        addPersona(Persona.BOBA_FETT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.Audience_Chamber)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        Filter abilityLessThanSelf = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getAbility(gameState, physicalCard) < modifiersQuerying.getAbility(gameState, self);
            }
        };

        Condition opponentHasMoreCharactersThanYouCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return (Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)))
                        > Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.here(self))));
            }
        };

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, opponentHasMoreCharactersThanYouCondition, 1, self.getOwner()));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self), abilityLessThanSelf), -1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Hidden_Weapons));
        return modifiers;
    }
}
