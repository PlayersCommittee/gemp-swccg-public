package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Organa's Ceremonial Necklace
 */
public class Card1_226 extends AbstractUtinniEffect {
    public Card1_226() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Organa's Ceremonial Necklace", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Necklace worn by Princess Leia during the awards ceremony after the Battle of Yavin. A powerful artifact in the Alderaanian royal family for dozens of generations.");
        setGameText("Deploy on any Yavin 4 site. Target one Imperial. When Imperial reaches target site, 'steal' necklace. Whenever necklace is present during Force drain: add 1 to Force drain and may then pass (for free) to an adjacent site you control (if any), and so on.");
        addKeywords(Keyword.FORCE_DRAIN_MULTI_PARTICIPANT);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Yavin_4_site;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.Imperial;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.Imperial;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        final String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("'Steal'");
            action.setActionMsg("Have " + GameUtils.getCardLink(target) + " 'steal' " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                            self.clearTargetedCards();
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, target));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.wherePresent(self))
                && GameConditions.isUtinniEffectReached(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to Force drain");
            // Perform result(s)
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // May pass to Imperial at adjacent site that you also control
                            final Filter imperialFilter = Filters.and(Filters.Imperial, Filters.at(Filters.and(Filters.adjacentSite(self), Filters.controls(playerId))));
                            if (GameConditions.canSpot(game, self, imperialFilter)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to pass " + GameUtils.getCardLink(self) + " to another Imperial?") {
                                                    @Override
                                                    protected void yes() {
                                                        final SubAction subAction = new SubAction(action);
                                                        // Choose target(s)
                                                        subAction.appendTargeting(
                                                                new ChooseCardOnTableEffect(action, playerId, "Choose an Imperial", imperialFilter) {
                                                                    @Override
                                                                    protected void cardSelected(final PhysicalCard receiver) {
                                                                        // Perform result(s)
                                                                        subAction.appendEffect(
                                                                                new AttachCardFromTableEffect(action, self, receiver));
                                                                    }
                                                                });
                                                        action.appendEffect(
                                                                new StackActionEffect(action, subAction));
                                                    }
                                                }
                                        ));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}