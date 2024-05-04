package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromLifeForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Cloud City
 * Type: Effect
 * Subtype: Utinni
 * Title: The Emperor's Prize
 */
public class Card5_124 extends AbstractUtinniEffect {
    public Card5_124() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "The Emperor's Prize", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Vader thought that by capturing Luke, he would crush the Alliance's last hope. But, there was another...");
        setGameText("If Luke was just 'frozen,' deploy on Emperor or Detention Block Corridor. Target Luke and Vader. When reached by targets, place Utinni Effect on Luke and opponent must lose half of Life Force (round down). If Luke released, lose Utinni Effect. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return false;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.frozen(game, effectResult, Filters.Luke)
                || TriggerConditions.frozenAndCaptured(game, effectResult, Filters.Luke)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Emperor, Filters.Detention_Block_Corridor);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.Emperor, Filters.Detention_Block_Corridor);
    }

    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        return Arrays.asList(TargetId.UTINNI_EFFECT_TARGET_1, TargetId.UTINNI_EFFECT_TARGET_2);
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        if (targetId == TargetId.UTINNI_EFFECT_TARGET_1) {
            return Filters.and(Filters.Luke, Filters.frozenCaptive);
        }
        else if (targetId == TargetId.UTINNI_EFFECT_TARGET_2) {
            return Filters.Vader;
        }
        else {
            return Filters.none;
        }
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        if (targetId == TargetId.UTINNI_EFFECT_TARGET_1) {
            return Filters.Luke;
        }
        else if (targetId == TargetId.UTINNI_EFFECT_TARGET_2) {
            return Filters.Vader;
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
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        PhysicalCard luke = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);
        PhysicalCard vader = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2);

        // Check inactive condition
        if (GameConditions.isOnlyCaptured(game, self)) {

            // Check condition(s)
            if (!GameConditions.isUtinniEffectReached(game, self)
                    && TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isAtLocation(game, self, Filters.sameLocation(luke))
                    && GameConditions.isAtLocation(game, self, Filters.sameLocation(vader))) {
                int amountToLose = gameState.getPlayerLifeForce(opponent) / 2;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Transfer to " + GameUtils.getFullName(luke));
                action.setActionMsg("Transfer " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(luke));
                // Update usage limit(s)
                action.appendUsage(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                            }
                        }
                );
                // Perform result(s)
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, luke));
                action.appendEffect(
                        new LoseForceFromLifeForceEffect(action, opponent, amountToLose));
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, luke)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}