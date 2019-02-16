package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10
 * Type: Effect
 * Title: Ancient Watering Hole
 */

public class Card210_007 extends AbstractNormalEffect {
    public Card210_007() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ancient Watering Hole", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on table. Maz and your Rep are immune to attrition. While you have alien characters of five different species on table: your Force drains are +1, your total battle destiny is +1 (+2 if Maz or your Rep in battle), and your aliens are forfeit +1. [Immune to Alter]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_10, Icon.EPISODE_VII);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        final PhysicalCard rep = game.getGameState().getRep(self.getOwner());
        final Filter repFilter;
        if (rep != null) {
            repFilter = Filters.sameTitle(rep);
        }
        else {
            repFilter = null;
        }

        List<Modifier> modifiers = new LinkedList<Modifier>();

        if (rep != null){
            modifiers.add(new ImmuneToAttritionModifier(self, repFilter)); // For sure working
        }
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Maz)); // Untested since Maz currently doesn't exist, but should work

        modifiers.add(new TotalBattleDestinyModifier(self, new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                int retval = 0;
                // Jim: The IDE complains if I count the species earlier and try to use the the values in here.
                //  the value in here.  Don't know enough about Java to know why that is.  (Probably I'm doing something wrong)
                //  So I'm counting it inside this modifier declaration.
                int numSpecies = 0;
                if (rep != null) {
                    InBattleEvaluator eval = new InBattleEvaluator(self, Filters.or(Filters.sameTitle(rep), Filters.Maz));
                } else {
                    InBattleEvaluator eval = new InBattleEvaluator(self, Filters.Maz);
                }


                for (Species specie: Species.values())
                {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner()))))
                    { // there are some species defined and included in Species.value() that can be associated with non-alien characters.
                        // like Alderannian (a rebel Leia) or Correlian. (A rebel Han)  Hence the need for Filter.and(<that specie>, <alien>)
                        numSpecies++;
                    }
                }
                PhysicalCard repOnTable = null;
                if (rep != null) {
                    repOnTable = Filters.findFirstActive(game, self, Filters.sameTitle(rep));
                }
                PhysicalCard mazOnTable = Filters.findFirstActive(game, self, Filters.Maz);

                if ((numSpecies >= 5) && GameConditions.isInBattle(game, mazOnTable))
                    retval=2;
                else if ((numSpecies >= 5) && rep != null && GameConditions.isInBattle(game, repOnTable))
                    retval=2;
                else if (numSpecies >= 5)
                    retval = 1;
                else
                    retval = 0;

                return retval;
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                // The example I copied from had this second, overloaded version of evaluateExpression, don't know enough to know if I can
                //  delete this.
                int retval = 0;
                int numSpecies = 0;
                for (Species specie: Species.values())
                {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner()))))
                    {
                        numSpecies++;
                    }
                }
                PhysicalCard repOnTable = null;
                if (rep != null) {
                    repOnTable = Filters.findFirstActive(game, self, Filters.sameTitle(rep));
                }
                PhysicalCard mazOnTable = Filters.findFirstActive(game, self, Filters.Maz);

                if ((numSpecies >= 5) && GameConditions.isInBattle(game, mazOnTable))
                    retval=2;
                else if ((numSpecies >= 5) && (rep != null) && GameConditions.isInBattle(game, repOnTable))
                    retval=2;
                else if (numSpecies >= 5)
                    retval = 1;
                else
                    retval = 0;

                return retval;
            }
        }, self.getOwner()));

        modifiers.add(new ForceDrainModifier(self, Filters.location, new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                int numSpecies = 0;
                for (Species specie: Species.values())
                {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner()))))
                    {
                        numSpecies++;
                    }
                }
                if (numSpecies >= 5)
                    return 1;
                else
                    return 0;
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                int numSpecies = 0;
                for (Species specie: Species.values())
                {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner()))))
                    {
                        numSpecies++;
                    }
                }
                if (numSpecies >= 5)
                    return 1;
                else
                    return 0;
            }
        }, self.getOwner()));

        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.owner(self.getOwner()), Filters.alien), new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                int numSpecies = 0;
                for (Species specie : Species.values()) {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner())))) {
                        numSpecies++;
                    }
                }
                if (numSpecies >= 5)
                    return 1;
                else
                    return 0;
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                int numSpecies = 0;
                for (Species specie : Species.values()) {
                    if (GameConditions.canSpot(game, self, 1, false, Filters.and(Filters.species(specie), Filters.alien, Filters.your(self.getOwner())))) {
                        numSpecies++;
                    }
                }
                if (numSpecies >= 5)
                    return 1;
                else
                    return 0;
            }
        }));

        return modifiers;
    }

}