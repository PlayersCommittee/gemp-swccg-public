package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 10
 * Type: Character
 * Subtype: Jedi Master
 * Title: Plo Koon (V)
 */
public class Card210_023 extends AbstractJediMaster {
    public Card210_023() {
        super(Side.LIGHT, 2, 5, 5, 7, 5, "Plo Koon", Uniqueness.UNIQUE);
        setLore("Kel Dor Jedi Council member descended from a long line of Jedi. Known for exceptional sensory skills, Plo Koon's control of the Force is unmatched by most.");
        setGameText("Deploys -2 to same location as Ahsoka (or vice versa). Once per turn, may lose top card of Force Pile to make your character (or piloted [Episode I] starfighter) here forfeit +2 and immune to attrition for remainder of turn.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.PILOT);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
        setSpecies(Species.KEL_DOR);
        setVirtualSuffix(true);
    }


    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -2, Filters.sameLocationAs(self, Filters.Ahsoka)));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Ahsoka, -2, Filters.here(self)));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter yourCharacterHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));
        Filter yourEp1StarshipHere = Filters.and(Filters.your(self), Filters.starship, Icon.EPISODE_I, Filters.here(self));
        Filter characterOrStarship = Filters.or(yourCharacterHere, yourEp1StarshipHere);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose top card of force pile");
            action.setActionMsg("Make a character forfeit + 2 and immune to attrition for remainder of turn ");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Apply Costs
            action.appendCost(
                    new LoseTopCardOfForcePileEffect(action, playerId)
            );

            // Perform result(s)
            action.appendEffect(
                    new ChooseCardOnTableEffect(action, self.getOwner(), "Choose character or EP1 starship here", characterOrStarship) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.addAnimationGroup(selectedCard);

                            action.appendEffect(
                                    new ModifyForfeitUntilEndOfTurnEffect(action, selectedCard, 2)
                            );
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new ImmuneToAttritionModifier(self, selectedCard),
                                            "Makes " + GameUtils.getCardLink(selectedCard) + " immune to attrition"));
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }

}
