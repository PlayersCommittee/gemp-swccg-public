package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Objective
 * Title: The First Order Reigns / The Resistance Is Doomed
 */
public class Card225_032 extends AbstractObjective {
    public Card225_032() {
        super(Side.DARK, 0, "The First Order Reigns", ExpansionSet.SET_25, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Crait and D'Qar systems, Supremacy: Bridge, and Tracked Fleet. For remainder of game, you may not deploy cards with ability except [Episode VII] cards. Your [Episode VII] characters and starships are power +1. Supremacy is deploy -9 to [Episode VII] systems. Once per turn, may [download] an [Episode VII] battleground. While this side up, neither player loses more than 1 Force to Force drains at systems (unless Tracked Fleet there). Flip this card if Tracked Fleet is 'annihilated.'");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Dqar_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose D'Qar System to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Crait_system, true, false) {
                        @Override
                        public String getChoiceText() {
                        return "Choose Crait System to deploy";
                        }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Supremacy_Bridge, true, false) {
                        @Override
                        public String getChoiceText() {
                        return "Choose Supremacy: Bridge Site to deploy";
                        }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tracked_Fleet, true, false) {
                        @Override
                        public String getChoiceText() {
                        return "Choose Tracked Fleet";
                        }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        Filter yourEpisodeVIICharactersAndStarships = Filters.and(Filters.your(self), Icon.EPISODE_VII, Filters.or(Filters.character, Filters.starship));
        Filter mayNotDeployRestrictionFilter = Filters.and(Filters.your(self), Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Icon.EPISODE_VII));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, mayNotDeployRestrictionFilter, playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new PowerModifier(self, yourEpisodeVIICharactersAndStarships, 1), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostToLocationModifier(self, Filters.Supremacy, -9, Filters.and(Icon.EPISODE_VII, Filters.system)), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_FIRST_ORDER_REIGNS__DOWNLOAD_EPISODE_7_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy an [Episode VII] battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_VII, Filters.battleground), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.system, Filters.not(Filters.hasAttached(Filters.Tracked_Fleet))), 1, playerId));
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.system, Filters.not(Filters.hasAttached(Filters.Tracked_Fleet))), 1, opponent));
        return modifiers;
    }

    // Flip Trigger
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isBlownAway(game, Filters.Tracked_Fleet)) {

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
