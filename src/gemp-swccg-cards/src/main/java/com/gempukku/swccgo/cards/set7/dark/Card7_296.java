package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Objective
 * Title: Carbon Chamber Testing / My Favorite Decoration
 */
public class Card7_296 extends AbstractObjective {
    public Card7_296() {
        super(Side.DARK, 0, Title.Carbon_Chamber_Testing, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Carbonite Chamber, Carbonite Chamber Console and Security Tower with a Rebel (opponent's choice) from opponent's Reserve Deck (if possible) imprisoned there. While this side up, once during each of your deploy phases, you may deploy from Reserve Deck one Audience Chamber, Docking Bay 94 or East Platform; reshuffle. You may not play Dark Deal. Flip this card if you move a frozen captive to Audience Chamber (or if no Rebel was in opponent's Reserve Deck at start of game).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameState gameState = game.getGameState();
        String opponent = game.getOpponent(playerId);

        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Carbonite_Chamber, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Carbonite Chamber to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Carbonite_Chamber_Console, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Carbonite Chamber Console to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Security_Tower, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Security Tower to deploy";
                    }
                });

        // Special rule about deploying Jabba's Prize instead of a Rebel from opponent's Reserve Deck
        if (Filters.canSpot(gameState.getReserveDeck(playerId), game, Filters.Jabbas_Prize)) {
            action.appendRequiredEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Jabbas_Prize, Filters.Security_Tower, true, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), DeployAsCaptiveOption.deployAsImprisonedFrozenCaptive(), false) {
                        @Override
                        public String getChoiceText() {
                            return "Choose Jabba's Prize to deploy";
                        }
                    });
        }
        else {
            // Special rule about what Rebels can be imprisoned for when Light Side player is playing other specific objectives
            Filter filter = Filters.Rebel;
            PhysicalCard lightSideObjective = gameState.getObjectivePlayed(opponent);
            if (lightSideObjective != null) {
                if (Filters.title(Title.You_Can_Either_Profit_By_This).accepts(game, lightSideObjective)) {
                    filter = Filters.and(filter, Filters.not(Filters.Han));
                }
                if (Filters.title(Title.Rescue_The_Princess).accepts(game, lightSideObjective)) {
                    filter = Filters.and(filter, Filters.not(Filters.Leia));
                }
                if (Filters.title(Title.There_Is_Good_In_Him).accepts(game, lightSideObjective)) {
                    filter = Filters.and(filter, Filters.not(Filters.Luke));
                }
            }
            action.appendOptionalEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, opponent, filter, Filters.Security_Tower, true, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), DeployAsCaptiveOption.deployAsImprisonedCaptive(), false) {
                        @Override
                        public String getChoiceText() {
                            return "Choose Rebel to deploy into Security Tower as 'imprisoned' captive";
                        }
                    });
        }
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.imprisonedIn(Filters.Security_Tower))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg(null);
            // Set indicator that no Rebel was imprisoned at start of game and flip Objective
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return action;
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CARBON_CHAMBER_TESTING__DOWNLOAD_AUDIENCE_CHAMBER_DOCKING_BAY_94_OR_EAST_PLATFORM;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Audience_Chamber, Title.Docking_Bay_94, Title.East_Platform))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Audience Chamber, Docking Bay 94, or East Platform from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Audience_Chamber, Filters.Docking_Bay_94, Filters.East_Platform), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, Filters.Dark_Deal, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.movedToLocationBy(game, effectResult, playerId, Filters.escorting(Filters.frozenCaptive), Filters.Audience_Chamber)
                && GameConditions.canBeFlipped(game, self)) {

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