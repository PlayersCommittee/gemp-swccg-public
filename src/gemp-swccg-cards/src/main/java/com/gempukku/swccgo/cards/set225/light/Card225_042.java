package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Lost
 * Title: Courage Of A Skywalker (V)
 */
public class Card225_042 extends AbstractLostInterrupt {
    public Card225_042() {
        super(Side.LIGHT, 2, Title.Courage_Of_A_Skywalker, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Despite being alone, trapped and desperately outmatched, Luke continued his battle with the Dark Lord of the Sith.");
        setGameText("If your Skywalker in battle alone with a character of greater ability, add one battle destiny. OR Deploy a lightsaber on a Skywalker from Lost Pile (or Force Pile; reshuffle). OR Once per game, during a battle or duel involving a Skywalker, make a just drawn destiny = 2.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter filterLoneSkywalker = Filters.and(Filters.your(playerId), Filters.Skywalker, Filters.participatingInBattle, Filters.alone);
        // Check condition(s)
        if (GameConditions.canTarget(game, self, filterLoneSkywalker)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PhysicalCard loneSkywalker = Filters.findFirstActive(game, self, Filters.and(filterLoneSkywalker, Filters.canBeTargetedBy(self)));

            if (loneSkywalker != null) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                final float ability = modifiersQuerying.getAbility(gameState, loneSkywalker);
                final Filter otherCharacterFilter = Filters.and(Filters.character, Filters.participatingInBattle, Filters.abilityMoreThan(ability));

                if (GameConditions.canTarget(game, self, otherCharacterFilter)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);

                    action.setText("Add one battle destiny");
                    // Allow response(s)
                    action.allowResponses(
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new AddBattleDestinyEffect(action, 1));
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }

        gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER__DEPLOY_LIGHTSABER_FROM_FORCE_PILE;
        // Check condition(s)
        if (GameConditions.canDeployCardFromForcePile(game, playerId, self, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.Skywalker)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy lightsaber from Force Pile");
            action.setActionMsg("Deploy a lightsaber on a Skywalker from Force Pile");
            // Allow response(s)
            action.allowResponses("Deploy a lightsaber on a Skywalker from Force Pile",
                new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromForcePileEffect(action, Filters.lightsaber, Filters.Skywalker, true));
                        }
                }  
            );
            actions.add(action);     
        }

        gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER__DEPLOY_LIGHTSABER_FROM_LOST_PILE;
        // Check condition(s)
        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.Skywalker)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy lightsaber from Lost Pile");
            action.setActionMsg("Deploy a lightsaber on a Skywalker from Lost Pile");
            // Allow response(s)
            action.allowResponses("Deploy a lightsaber on a Skywalker from Lost Pile",
                new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, Filters.Skywalker, false));
                        }
                }  
            );
            actions.add(action);     
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.COURAGE_OF_A_SKYWALKER_V__DESTINY_EQUALS_2;
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (GameConditions.isDuringBattleWithParticipant(game, Filters.Skywalker)
                || GameConditions.isDuringDuelWithParticipant(game, Filters.Skywalker))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Set destiny to 2");
            action.appendUsage(new OncePerGameEffect(action));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new ResetDestinyEffect(action, 2));
                }
            });
            actions.add(action);
        }

        return actions;
    }
}
