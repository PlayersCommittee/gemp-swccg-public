package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.TotalAbilityLessThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Caldera Righim
 */
public class Card11_001 extends AbstractAlien {
    public Card11_001() {
        super(Side.LIGHT, 2, 3, 2, 2, 3, "Caldera Righim", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Male Talz. Somewhat of a pacifist, Caldera has always been an opponent to violence in the cantina. Personal friend of Wuher.");
        setGameText("Unless Great Warrior on table, opponent must have total ability < 8 to initiate a battle at same site. Also, whenever a battle is initiated at same site both players may draw up to 2 cards from top of their Reserve Deck.");
        addIcons(Icon.TATOOINE);
        setSpecies(Species.TALZ);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, Filters.sameSite(self),
                new AndCondition(new UnlessCondition(new OnTableCondition(self, Filters.Great_Warrior)),
                        new NotCondition(new TotalAbilityLessThanCondition(opponent, 8, Filters.sameSite(self)))), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSite(self))) {
            int maxCardsToDraw = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxCardsToDraw > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw cards from Reserve Deck");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose number of cards to draw ", 1, maxCardsToDraw, maxCardsToDraw) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Draw " + result + " card" + GameUtils.s(result) + " from top of Reserve Deck");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSite(self))) {
            int maxCardsToDraw = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxCardsToDraw > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Draw cards from Reserve Deck");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose number of cards to draw ", 1, maxCardsToDraw, maxCardsToDraw) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Draw " + result + " card" + GameUtils.s(result) + " from top of Reserve Deck");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
