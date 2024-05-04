package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Daultay Dofine
 */
public class Card12_103 extends AbstractRepublic {
    public Card12_103() {
        super(Side.DARK, 2, 2, 2, 3, 4, "Daultay Dofine", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Neimoidian Trade Federation captain who gained his current position through political backstabbing and family connections. Not favored by Darth Sidious.");
        setGameText("At start of your draw phase, if you have no cards in your Force Pile may activate up to two Force. If your just drawn destiny was a destroyer droid or battle droid, once per turn you may take that card into hand to cancel that destiny and re-draw.");
        addPersona(Persona.DOFINE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.DRAW)
                && !GameConditions.hasForcePile(game, playerId)
                && GameConditions.canActivateForce(game, playerId)) {
            int maxForceToActivate = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            if (maxForceToActivate > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Activate Force");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, maxForceToActivate, maxForceToActivate) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.setActionMsg("Activate " + result + " Force");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result));
                                    }
                                }
                        ));
                actions.add(action);
            }
        }

        // Card action 2
        gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.or(Filters.destroyer_droid, Filters.battle_droid))
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            actions.add(action);
        }

        return actions;
    }
}
