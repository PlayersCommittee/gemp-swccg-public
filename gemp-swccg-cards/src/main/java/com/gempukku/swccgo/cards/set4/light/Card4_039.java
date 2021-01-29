package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackRandomCardsFromHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInHandEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Stone Pile
 */
public class Card4_039 extends AbstractNormalEffect {
    public Card4_039() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, Title.Stone_Pile, Uniqueness.RESTRICTED_2);
        setLore("'Use the Force. Yes... Now, the stone. Feel it.");
        setGameText("Use 2 Force to deploy on any Dagobah site. Randomly select two cards from opponent's hand and place them, unseen, face down beneath Stone Pile. Cards return to opponent's hand if Effect leaves table. Canceled if opponent occupies this site.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dagobah_site;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameState gameState = game.getGameState();
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            boolean canRemoveCardsFromOpponentsHand = !game.getModifiersQuerying().mayNotRemoveCardsFromOpponentsHand(gameState, self, playerId);
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Stack two random cards from opponent's hand");
            // Perform result(s)

            // Check first if player is allowed to remove cards from opponent's hand
            if (canRemoveCardsFromOpponentsHand) {
                action.appendEffect(new StackRandomCardsFromHandEffect(action, playerId, opponent, self, true, 2));
            }
            else {
                action.appendEffect(new SendMessageEffect(action,playerId + " is not allowed to remove cards from " + opponent + "'s hand" ));
            }
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.occupies(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameSite(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && GameConditions.hasStackedCards(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Return stacked cards to hand");
            // Perform result(s)
            action.appendEffect(
                    new PutStackedCardsInHandEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}