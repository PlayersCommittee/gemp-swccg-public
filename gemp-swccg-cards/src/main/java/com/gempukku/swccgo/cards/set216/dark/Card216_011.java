package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Objective
 * Title: On The Verge Of Greatness / Taking Control Of The Weapon
 */
public class Card216_011 extends AbstractObjective {
    public Card216_011() {
        super(Side.DARK, 0, Title.On_The_Verge_Of_Greatness);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy [Set 16] Death Star and Scarif systems, Citadel Tower, Commence Primary Ignition, and Shield Gate. \n" +
                "For remainder of game, you may not deploy characters of ability > 4 (except Vader). Vader is power +2 and he (or a Star Destroyer he is piloting) may make a regular move to a battle just initiated. \n" +
                "While this side up, once per turn, may [download] a site (or Imperial trooper) to Scarif. \n" +
                "Flip this card if Krennic or Tarkin on Scarif and Death Star orbiting Scarif.");
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.VIRTUAL_SET_16, Filters.Death_Star_system), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Set 16] Death Star system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Scarif_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Scarif system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Scarif_Citadel_Tower), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Citadel Tower to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Commence_Primary_Ignition, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Commence Primary Ignition to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Shield_Gate, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Shield Gate to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ON_THE_VERGE_OF_GREATNESS__DEPLOY_SITE_OR_TROOPER_TO_SCARIF;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a site or Imperial trooper to Scarif");
            action.setActionMsg("Deploy a site or Imperial trooper to Scarif");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.site, Filters.and(Filters.trooper, Filters.Imperial)), Title.Scarif, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.and(Filters.except(Filters.Vader), Filters.character, Filters.abilityMoreThan(4)), self.getOwner()), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new PowerModifier(self, Filters.Vader, 2), null));
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult) {

                                // Vader... (or a Star Destroyer he is piloting) may make a regular move to a battle just initiated.
                                Filter vaderOrStarDestroyerHePilots = Filters.and(Filters.or(Filters.Vader, Filters.and(Filters.Star_Destroyer, Filters.hasPiloting(self, Filters.Vader)))
                                        , Filters.movableAsRegularMove(playerId, false, 0, false, Filters.locationAndCardsAtLocation(Filters.battleLocation)));

                                if (TriggerConditions.battleInitiated(game, effectResult)
                                        && GameConditions.canTarget(game, self, vaderOrStarDestroyerHePilots)) {
                                    final PhysicalCard battleLocation = game.getGameState().getBattleLocation();

                                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                    action.setPerformingPlayer(playerId);
                                    action.setText("Move Vader or a Star Destroyer he pilots to battle");
                                    action.setActionMsg("Move Vader or a Star Destroyer he is piloting as a regular move to a battle just initiated");
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose Vader or Star Destroyer", vaderOrStarDestroyerHePilots) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                    action.addAnimationGroup(targetedCard);
                                                    action.addAnimationGroup(battleLocation);
                                                    // Allow response(s)
                                                    action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(battleLocation),
                                                            new RespondableEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new MoveCardAsRegularMoveEffect(action, playerId, finalTarget, false, false, Filters.locationAndCardsAtLocation(Filters.battleLocation)));
                                                                }
                                                            }
                                                    );
                                                }
                                            }
                                    );

                                    return Collections.singletonList((TriggerAction)action);
                                }
                                return null;
                            }
                        }
                ));
        return action;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.on(Title.Scarif), Filters.or(Filters.Tarkin, Filters.Krennic)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Death_Star_system, Filters.isOrbiting(Title.Scarif)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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