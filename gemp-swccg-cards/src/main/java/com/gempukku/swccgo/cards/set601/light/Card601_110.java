package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Subtype: Immediate
 * Title: Imperial Atrocity (V)
 */
public class Card601_110 extends AbstractImmediateEffect {
    public Card601_110() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Atrocity, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire's ruthless tactics at times unintentionally create support for the cause of the Rebel Alliance.");
        setGameText("If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card) that loss is cumulatively -1 (to a minimum of 1). Non-[Virtual Block 4] Imperial Atrocity is canceled.");
        //TODO the image online doesn't have the errata that added "Non-[Virtual Block 4] Imperial Atrocity is canceled."
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                action.appendBeforeCost(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(game.getGameState().getTopForceLossState())));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.not(Icon.LEGACY_BLOCK_4), Filters.Imperial_Atrocity))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && self.getWhileInPlayData() != null) {

            //TODO make sure this does what it should according to https://forum.starwarsccg.org/viewtopic.php?p=680654#p680654

            //first reduce it by 2
            if (GameConditions.canReduceForceLoss(game)
                    && self.getWhileInPlayData().getForceLossState() != null) {

                ForceLossState forceLossState = self.getWhileInPlayData().getForceLossState();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Reduce Force loss by 2");

                action.appendUsage(
                        new OncePerForceLossEffect(action));
                action.appendCost(
                        new SetWhileInPlayDataEffect(action, self, null));
                // Perform result(s)
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, 2, 0, false, forceLossState));
/*
                if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                        && GameConditions.canReduceForceLoss(game)) {

                    //need to check if it's from my card or a force drain at a battleground without being able to use TriggerConditions because the current EffectResult is this card just being deployed
                    //!TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.or(Filters.your(self), Filters.immuneToCardTitle(self.getTitle())))
                    //!TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.battleground)

                    LoseForceEffect loseForceEffect = forceLossState.getLoseForceEffect();
                    boolean ableToReduce = false;
                    if (loseForceEffect.isForceDrain()) {
                        PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();
                        if (!Filters.battleground.accepts(game, forceDrainLocation))
                            ableToReduce = true;
                    } else if (loseForceEffect.getAction() != null){
                        PhysicalCard source = loseForceEffect.getAction().getActionSource();
                        if (source != null) {
                            Filter myCardOrIsImmuneToThis = Filters.or(Filters.your(self), Filters.immuneToCardTitle(self.getTitle()));
                            if (!myCardOrIsImmuneToThis.accepts(game, source))
                                ableToReduce = true;
                        } else {
                            //not sure about this one. allows reducing if the source is null
                            ableToReduce = true;
                        }
                    }

                    if (ableToReduce) {
                        action.appendEffect(new SendMessageEffect(action, playerId + " targets to cumulatively reduce Force loss by 1 (to a minimum of 1) using " + GameUtils.getCardLink(self)));
                        action.appendEffect(
                                new ReduceForceLossEffect(action, playerId, 1, 1, false, forceLossState));
                    }
                }
*/
                actions.add(action);
            }
        }
/*
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.or(Filters.your(self), Filters.immuneToCardTitle(self.getTitle())))
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.battleground)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Cumulatively reduce Force loss by 1");
            action.setActionMsg("Cumulatively reduce Force loss by 1 (to a minimum of 1)");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReduceForceLossEffect(action, playerId, 1, 1));
            actions.add(action);
        }*/


        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.and(Filters.not(Icon.LEGACY_BLOCK_4), Filters.Imperial_Atrocity))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.and(Filters.not(Icon.LEGACY_BLOCK_4), Filters.Imperial_Atrocity), Title.Imperial_Atrocity);
            actions.add(action);
        }

        return actions;
    }
}