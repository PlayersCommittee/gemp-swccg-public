package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Objective
 * Title: Zero Hour / Liberation of Lothal
 */
public class Card219_048 extends AbstractObjective {
    public Card219_048() {
        super(Side.LIGHT, 0, "Zero Hour");
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Lothal system and a Lothal site." +
                "For remainder of game, Menace Fades and Projection Of A Skywalker are canceled. " +
                "Jedi (except Ahsoka and Kanan), Resistance characters, and [Resistance] starships are deploy +1. " +
                "Chopper, Ezra, Hera, Kanan, Sabine, and Zeb gain Phoenix Squadron. Once per turn, may [download] a Lothal site." +
                "Flip this card if Rebels control three Lothal locations (or you occupy three Lothal locations with Phoenix Squadron characters) and opponent controls no Lothal locations.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Lothal_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Lothal system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Lothal_site, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose a Lothal site to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostModifier(self, Filters.or(Filters.and(Filters.Jedi, Filters.except(Filters.or(Filters.Ahsoka, Filters.Kanan))), Filters.Resistance_character, Filters.and(Icon.RESISTANCE, Filters.starship)), 1),
                        null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new KeywordModifier(self, Filters.or(Filters.Chopper, Filters.Ezra, Filters.Hera, Filters.Kanan, Filters.Sabine, Filters.Zeb), Keyword.PHOENIX_SQUADRON),
                        null));
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.title(Title.Menace_Fades), Filters.title(Title.Projection_Of_A_Skywalker)))
                                        && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

                                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                                    actions.add(action);
                                }
                                return actions;
                            }
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isTableChanged(game, effectResult)
                                        && GameConditions.canTargetToCancel(game, self, Filters.title(Title.Menace_Fades))) {

                                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Menace_Fades), Title.Menace_Fades);
                                    actions.add(action);
                                }
                                // Check condition(s)
                                if (TriggerConditions.isTableChanged(game, effectResult)
                                        && GameConditions.canTargetToCancel(game, self, Filters.title(Title.Projection_Of_A_Skywalker))) {

                                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Projection_Of_A_Skywalker), Title.Projection_Of_A_Skywalker);
                                    actions.add(action);
                                }
                                return actions;
                            }
                        }
                ));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ZERO_HOUR__DEPLOY_LOCATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Lothal site from Reserve Deck");
            action.setActionMsg("Deploy a Lothal site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Lothal_site, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Track if you have force drained this turn
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId)) {
            self.setWhileInPlayData(new WhileInPlayData(true));
        }

        // Reset at the end of each turn
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.controlsWith(game, self, playerId, 3, Filters.Lothal_location, Filters.Rebel)
                || GameConditions.occupiesWith(game, self, playerId, 3, Filters.Lothal_location, Filters.Phoenix_Squadron_character))
                && !GameConditions.controls(game, opponent, Filters.Lothal_location)) {


            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);

        }
        return null;
    }
}
