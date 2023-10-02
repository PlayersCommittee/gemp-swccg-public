package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: Alien
 * Title: Black Krrsantan
 */
public class Card222_004 extends AbstractAlien {
    public Card222_004() {
        super(Side.DARK, 2, 4, 6, 2, 5, "Black Krrsantan", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setArmor(5);
        setLore("Wookiee bounty hunter.");
        setGameText("Vibro-Ax deploys free on Krrsantan. Power +1 while defending a battle. " +
                "If a battle was just initiated here, each player who has four or more characters present with Krrsantan must choose one to return to hand.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        setSpecies(Species.WOOKIEE);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Vibro_Ax, self));
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Filter charactersHere = Filters.and(Filters.here(self), Filters.presentWith(self), Filters.character);

        Filter opponentsCharactersHere = Filters.and(Filters.opponents(playerId), charactersHere);
        Filter yourCharactersHere = Filters.and(Filters.your(playerId), charactersHere);

        int numOpponentCharactersHere = Filters.countActive(game, self, opponentsCharactersHere);
        int numYourCharactersHere = Filters.countActive(game, self, yourCharactersHere);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && numOpponentCharactersHere >= 4) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Have " + opponent + " return a character to hand");
            // Perform result(s)
            action.appendEffect(
                    new ChooseCardOnTableEffect(action, opponent, "Choose character to return to your hand", Filters.and(opponentsCharactersHere, Filters.except(Filters.title("Black Krrsantan")))) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new ReturnCardToHandFromTableEffect(action, selectedCard)
                            );
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && numYourCharactersHere >= 4) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Have " + playerId + " return a character to hand");
            // Perform result(s)
            action.appendEffect(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to return to your hand", Filters.and(yourCharactersHere, Filters.except(Filters.title("Black Krrsantan")))) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new ReturnCardToHandFromTableEffect(action, selectedCard)
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
