package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithImprisonmentEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 15
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself (V)
 */
public class Card215_017 extends AbstractObjective {
    public Card215_017() {
        super(Side.LIGHT, 0, Title.Rescue_The_Princess, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Central Core, A Power Loss, Detention Block Corridor (with [A New Hope] Leia imprisoned there), and Trash Compactor. " +
                "For remainder of game, your Death Star sites generate +1 Force for you. You may not deploy Luke of ability > 4 or [Episode I] (or [Episode VII]) Jedi. If Leia is about to leave table (for any reason, even if inactive), imprison her in Detention Block Corridor (cards on her are placed in owner's Used Pile). Once per turn, may ▼ a Death Star site. " +
                "Flip this card if Leia occupies a Death Star site and A Power Loss is 'shut down.'");
        addIcons(Icon.A_NEW_HOPE, Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameState gameState = game.getGameState();

        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Death_Star_Central_Core, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Central Core to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.A_Power_Loss, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose A Power Loss to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Trash_Compactor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Trash Compactor to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Detention_Block_Corridor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Detention Block to deploy";
                    }
                });

        if (Filters.canSpot(gameState.getReserveDeck(playerId), game, Filters.Detention_Block_Corridor)) {
            action.appendRequiredEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.and(Icon.A_NEW_HOPE, Filters.Leia), Filters.Detention_Block_Corridor, true, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), DeployAsCaptiveOption.deployAsImprisonedCaptive(), false) {
                        @Override
                        public String getChoiceText() {
                            return "Choose [A New Hope] Leia to deploy";
                        }
                    });
        }

        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourDeathStarSites = Filters.and(Filters.your(playerId), Filters.Death_Star_site);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceGenerationModifier(self, yourDeathStarSites, 1, playerId));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.Luke, Filters.abilityMoreThan(4)), Filters.and(Filters.Jedi, Filters.or(Filters.icon(Icon.EPISODE_I), Filters.icon(Icon.EPISODE_VII)))), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RESCUE_THE_PRINCESS_V__DOWNLOAD_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Death Star site from Reserve Deck");
            action.setActionMsg("Deploy a Death Star site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Death_Star_site, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();
        if (GameConditions.canBeFlipped(game, self)
                && GameConditions.occupiesWith(game, self, playerId, Filters.Death_Star_site, Filters.Leia)
                && GameConditions.isDeathStarPowerShutDown(game)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.appendEffect(
                    new FlipCardEffect(action, self)
            );
            actions.add(action);
        }

        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, Filters.Leia)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard leia = result.getCardAboutToLeaveTable();
            final PhysicalCard detentionBlockCorridor = Filters.findFirstFromTopLocationsOnTable(game, Filters.Detention_Block_Corridor);

            if (leia != null
                    && detentionBlockCorridor != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Imprison Leia");
                action.setPerformingPlayer(playerId);
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                Collection<PhysicalCard> cardsOnLeia = Filters.filterAllOnTable(game, Filters.attachedTo(leia));

                                result.getPreventableCardEffect().preventEffectOnCard(leia);
                                action.appendEffect(
                                        new RestoreCardToNormalEffect(action, leia));
                                action.appendEffect(
                                        new PlaceCardsInUsedPileFromTableEffect(action, cardsOnLeia));
                                action.appendEffect(
                                        new CaptureWithImprisonmentEffect(action, leia, detentionBlockCorridor, leia.isUndercover(), leia.isMissing()));
                            }
                        });
                actions.add(action);
            }

        }

        return actions;
    }
}
