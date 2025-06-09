package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Starship
 * Subtype: Capital
 * Title: Profundity
 */
public class Card207_018 extends AbstractCapitalStarship {
    public Card207_018() {
        super(Side.LIGHT, 1, 6, 6, 7, null, 3, 8, Title.Profundity, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setGameText("Deploys -2 to a system opponent occupies (-4 if opponent's system). May add 6 pilots, 8 passengers, 3 starfighters, 3 vehicles, and 1 corvette. Permanent pilot provides ability of 2.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_7);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setStarfighterCapacity(3);
        setVehicleCapacity(3);
        setCapitalStarshipCapacity(1, Filters.corvette);
        addPersona(Persona.PROFUNDITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        final Evaluator evaluator = new CardMatchesEvaluator(-2, -4, Filters.locationAndCardsAtLocation(Filters.and(Filters.system, Filters.opponents(self))));
        final Filterable locationFilter = Filters.locationAndCardsAtLocation(Filters.and(Filters.system, Filters.occupies(opponent)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, evaluator, locationFilter) {
            @Override
            public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
                if (Filters.and(locationFilter).accepts(gameState, modifiersQuerying, target)) {
                    return evaluator.evaluateExpression(gameState, modifiersQuerying, target);
                }
                return 0;
            }
        });
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
