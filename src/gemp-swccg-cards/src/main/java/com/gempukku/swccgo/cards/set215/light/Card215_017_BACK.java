package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureWithImprisonmentEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
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
public class Card215_017_BACK extends AbstractObjective {
    public Card215_017_BACK() {
        super(Side.LIGHT, 7, Title.Sometimes_I_Amaze_Even_Myself, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setGameText("While this side up, for opponent to initiate a Force drain, opponent must use +1 Force. Your Death Star sites are immune to Set Your Course For Alderaan. Once per turn, if you just 'hit' a character with a blaster, opponent loses 1 Force. May place Obi-Wan out of play from a Death Star site to cancel a battle just initiated anywhere on Death Star. I Can't Believe He's Gone is canceled. During opponent's draw phase, if opponent did not initiate a battle this turn, may retrieve 1 Force. " +
                "Flip this card if Leia is not at a Death Star site.");
        addIcons(Icon.A_NEW_HOPE, Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
        hideFromDeckBuilder();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourDeathStarSites = Filters.and(Filters.your(playerId), Filters.Death_Star_site);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceGenerationModifier(self, yourDeathStarSites, 1, self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.Luke, Filters.abilityMoreThan(4)), Filters.and(Filters.Jedi, Filters.or(Filters.icon(Icon.EPISODE_I), Filters.icon(Icon.EPISODE_VII)))), playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Set_Your_Course_For_Alderaan, ModifyGameTextType.SET_YOUR_COURSE_FOR_ALDERAAN__ONLY_AFFECTS_DARK_SIDE_DEATH_STAR_SITES));
        modifiers.add(new InitiateForceDrainCostModifier(self, 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        List<TopLevelGameTextAction> actions = new LinkedList<>();

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
            actions.add(action);
        }


        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.DRAW)
                && !GameConditions.hasInitiatedBattleThisTurn(game, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.I_Cant_Believe_Hes_Gone)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.at(Filters.Death_Star_site)))
                && TriggerConditions.battleInitiatedAt(game, effectResult, Filters.on(Title.Death_Star))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel battle");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, Filters.findFirstActive(game, self, Filters.ObiWan)));
            // Perform result(s)
            action.appendEffect(
                    new CancelBattleEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();
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

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.I_Cant_Believe_Hes_Gone)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.I_Cant_Believe_Hes_Gone, Title.I_Cant_Believe_Hes_Gone);
            actions.add(action);
        }

        if (GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Leia, Filters.at(Filters.Death_Star_site)))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.appendEffect(
                    new FlipCardEffect(action, self)
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.and(Filters.your(self.getOwner()), Filters.blaster))
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(self.getOwner()), 1)
            );
            actions.add(action);
        }

        return actions;
    }
}
