package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractPodracer;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DrawsRaceDestinyAndChooseModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsRaceDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Podracer
 * Title: Anakin's Podracer
 */
public class Card11_047 extends AbstractPodracer {
    public Card11_047() {
        super(Side.LIGHT, 3, Title.Anakins_Podracer);
        setLore("Built from Radon-Ulzer racing engines that Watto regarded as too burned-out to be of any use. New fuel injection subsystem created by Anakin radically increases thrust.");
        setGameText("Deploy on Podrace Arena. Draws 2 race destiny instead of 1. During your draw phase, if opponent has a higher race total than Anakin's Podracer, draw 3 race destiny next turn and choose 2. Once per game may take I Did It! into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsRaceDestinyModifier(self, self, new NotCondition(new InPlayDataSetCondition(self)), new ConstantEvaluator(2)));
        modifiers.add(new DrawsRaceDestinyAndChooseModifier(self, self, new InPlayDataSetCondition(self), 3, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        if(TriggerConditions.isStartOfYourTurn(game, effectResult, self)){
            self.setWhileInPlayData(null);
        }

        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isDuringYourPhase(game, self, Phase.DRAW)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            float anakinsPodracerRaceTotal = modifiersQuerying.getPodracerRaceTotal(gameState, self);
            float opponentsRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, opponent);
            if (opponentsRaceTotal > anakinsPodracerRaceTotal) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Draw 3 and choose 2 race destiny next turn");
                // Perform result(s)
                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANAKINS_PODRACER__UPLOAD_I_DID_IT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take I Did It! into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.I_Did_It, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}