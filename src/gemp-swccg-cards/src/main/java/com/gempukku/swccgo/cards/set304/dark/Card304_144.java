package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotAttemptJediTestsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Subtype: Utinni
 * Title: Sibling Bait
 */
public class Card304_144 extends AbstractUtinniEffect {
    public Card304_144() {
        super(Side.DARK, 7, PlayCardZoneOption.ATTACHED, Title.Sibling_Bait, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Kai is extremely protective of his siblings after they fled from Kamjin. There is nothing he wouldn't do to protect them and Kamjin means to exploit that.");
        setGameText("Deploy on Komilia, Hikaru or Rohan if captured or 'frozen.' Target Kai. Kai may not attempt Jedi Tests. Also, during each of opponent's draw phases, opponent loses 2 Force (3 if captive is 'frozen'). Utinni Effect canceled when reached by target. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return SpotOverride.INCLUDE_CAPTIVE;
    }

    @Override
    protected boolean isDagobahAllowed() {
        return true;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.or(Filters.Komilia, Filters.Hikaru, Filters.Rohan), Filters.captive);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.Komilia, Filters.Hikaru, Filters.Rohan);
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.Kai;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.Kai;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotAttemptJediTestsModifier(self, Filters.Kai));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check inactive condition
        if (!GameConditions.isOnlyCaptured(game, self))
            return null;

        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {
            int amountOfForce = Filters.frozenCaptive.accepts(game, self.getAttachedTo()) ? 3 : 2;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + opponent + " lose " + amountOfForce + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, amountOfForce));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, Filters.hasAttached(self))
                || (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isOnlyCaptured(game, self)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target)))
                && GameConditions.canBeCanceled(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }

        // Check inactive condition
        if (GameConditions.isOnlyCaptured(game, self)) {

            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

            // Check condition(s)
            // Check if reached end of control phase and action was not performed yet.
            if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.DRAW)
                    && GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)) {
                int amountOfForce = Filters.frozenCaptive.accepts(game, self.getAttachedTo()) ? 3 : 2;

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make " + opponent + " lose " + amountOfForce + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, amountOfForce));
                actions.add(action);
            }
        }
        return actions;
    }
}