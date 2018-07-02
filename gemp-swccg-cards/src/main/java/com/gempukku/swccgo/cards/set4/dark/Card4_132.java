package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Sudden Impact
 */
public class Card4_132 extends AbstractNormalEffect {
    public Card4_132() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Sudden Impact");
        setLore("An asteroid looming in one's path can force a quick decision. One must choose, but choose wisely. This pilot chose poorly.");
        setGameText("Deploy on any Effect or Utinni Effect (except those immune to Alter). During each of owner's draw phases, if you occupy an asteroid sector, owner must choose to either lose 1 Force or voluntarily cancel that Effect or Utinni Effect. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.or(Filters.Effect, Filters.Utinni_Effect), Filters.except(Filters.immune_to_Alter));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        final PhysicalCard attachedTo = self.getAttachedTo();
        if (attachedTo != null) {
            final String attachedToOwner = attachedTo.getOwner();
            if (GameConditions.isOnceDuringPlayersPhase(game, self, playerId, gameTextSourceCardId, attachedToOwner, Phase.DRAW)
                    && GameConditions.occupies(game, playerId, Filters.asteroid_sector)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Make " + attachedToOwner + " lose 1 Force or cancel " + GameUtils.getFullName(attachedTo));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                if (GameConditions.canBeCanceled(game, attachedTo)) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, attachedToOwner,
                                    new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose 1 Force", "Cancel " + GameUtils.getFullName(attachedTo)}) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            if (index == 0) {
                                                game.getGameState().sendMessage(attachedToOwner + " chooses to lose 1 Force");
                                                action.appendEffect(
                                                        new LoseForceEffect(action, attachedToOwner, 1, true));
                                            }
                                            else {
                                                game.getGameState().sendMessage(attachedToOwner + " chooses to cancel " + GameUtils.getCardLink(attachedTo));
                                                action.appendEffect(
                                                        new CancelCardOnTableEffect(action, attachedTo));
                                            }
                                        }
                                    }
                            )
                    );
                }
                else {
                    game.getGameState().sendMessage("Losing 1 Force is the only available choice");
                    action.appendEffect(
                            new LoseForceEffect(action, attachedToOwner, 1));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        final PhysicalCard attachedTo = self.getAttachedTo();
        if (attachedTo != null) {
            final String attachedToOwner = attachedTo.getOwner();
            if (TriggerConditions.isEndOfPlayersPhase(game, effectResult, Phase.DRAW, attachedToOwner)
                    && GameConditions.isOnceDuringPlayersPhase(game, self, playerId, gameTextSourceCardId, attachedToOwner, Phase.DRAW)
                    && GameConditions.occupies(game, playerId, Filters.asteroid_sector)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + attachedToOwner + " lose 1 Force or cancel " + GameUtils.getFullName(attachedTo));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                if (GameConditions.canBeCanceled(game, attachedTo)) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, attachedToOwner,
                                    new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Lose 1 Force", "Cancel " + GameUtils.getCardLink(attachedTo)}) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            if (index == 0) {
                                                game.getGameState().sendMessage(attachedToOwner + " chooses to lose 1 Force");
                                                action.appendEffect(
                                                        new LoseForceEffect(action, attachedToOwner, 1, true));
                                            }
                                            else {
                                                game.getGameState().sendMessage(attachedToOwner + " chooses to cancel " + GameUtils.getCardLink(attachedTo));
                                                action.appendEffect(
                                                        new CancelCardOnTableEffect(action, attachedTo));
                                            }
                                        }
                                    }
                            )
                    );
                }
                else {
                    game.getGameState().sendMessage("Losing 1 Force is the only available choice");
                    action.appendEffect(
                            new LoseForceEffect(action, attachedToOwner, 1));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}