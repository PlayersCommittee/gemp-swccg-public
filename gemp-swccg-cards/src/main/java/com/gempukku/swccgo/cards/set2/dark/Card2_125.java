package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: A New Hope
 * Type: Effect
 * Subtype: Utinni
 * Title: Spice Mines Of Kessel
 */
public class Card2_125 extends AbstractUtinniEffect {
    public Card2_125() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Spice_Mines_Of_Kessel, Uniqueness.UNIQUE);
        setLore("Captives sent to the Kessel spice mines spend the rest of their lives digging for glitterstim, a spice sold throughout the galaxy by smugglers and crime lords.");
        setGameText("Deploy on Kessel (may not be moved). Target one captive and one trooper to escort captive. When targets reach Utinni Effect, retrieve Force equal to captive's forfeit (captive and Utinni Effect lost).");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Kessel_system;
    }

    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        return Arrays.asList(TargetId.UTINNI_EFFECT_TARGET_1, TargetId.UTINNI_EFFECT_TARGET_2);
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        if (targetId == TargetId.UTINNI_EFFECT_TARGET_1) {
            return Filters.captive;
        }
        else if (targetId == TargetId.UTINNI_EFFECT_TARGET_2) {
            return Filters.and(Filters.your(self), Filters.trooper);
        }
        else {
            return Filters.none;
        }
    }

    @Override
    public Map<InactiveReason, Boolean> getTargetSpotOverride(TargetId targetId) {
        if (targetId == TargetId.UTINNI_EFFECT_TARGET_1) {
            return SpotOverride.INCLUDE_CAPTIVE;
        }
        else {
            return null;
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard captive = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);
        PhysicalCard trooper = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(captive))
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(trooper))) {
            float forceToRetrieve = game.getModifiersQuerying().getForfeit(gameState, captive);
            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SPICE_MINES_OF_KESSEL__ADD_4_TO_FORCE_RETRIEVED)) {
                forceToRetrieve += 4;
            }

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Retrieve " + GuiUtils.formatAsString(forceToRetrieve) + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new RecordUtinniEffectCompletedEffect(action, self));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, forceToRetrieve));
            action.appendEffect(
                    new LoseCardsFromTableEffect(action, Arrays.asList(captive, self), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, trooper)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(captive, Filters.captive))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Release " + GameUtils.getFullName(captive));
            action.setActionMsg("Release " + GameUtils.getCardLink(captive));
            // Perform result(s)
            action.appendEffect(
                    new ReleaseCaptiveEffect(action, captive));
            actions.add(action);
        }

        return actions;
    }
}