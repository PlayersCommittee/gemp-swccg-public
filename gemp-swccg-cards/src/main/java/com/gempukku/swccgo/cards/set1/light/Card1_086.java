package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ApplyAbilityToDrawBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Help Me Obi-Wan Kenobi
 */
public class Card1_086 extends AbstractUsedInterrupt {
    public Card1_086() {
        super(Side.LIGHT, 3, "Help Me Obi-Wan Kenobi");
        setLore("Leia sent a hologram plea, 'General Kenobi, years ago you served my father in the Clone Wars. Now he begs you to help him in his struggle against the Empire.'");
        setGameText("If Obi-Wan is at a site you control, he may apply any amount of his ability toward drawing a battle destiny at any other location. Ability he uses this way cannot be used again this turn. OR One Rebel may move as a 'react' to an adjacent battle site.");
        addKeywords(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final PhysicalCard obiWan = Filters.findFirstActive(game, self, Filters.and(Filters.ObiWan,
                    Filters.at(Filters.and(Filters.site, Filters.not(Filters.battleLocation), Filters.controls(playerId), Filters.canBeTargetedBy(self))), Filters.canBeTargetedBy(self)));
            if (obiWan != null) {
                final int maxAbilityToUse = (int) Math.floor(modifiersQuerying.getAbilityForBattleDestiny(gameState, obiWan));
                if (maxAbilityToUse > 0) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Apply Obi-Wan's ability for battle destiny");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan", obiWan) {
                                @Override
                                protected boolean getUseShortcut() {
                                    return true;
                                }
                                @Override
                                protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedObiWan) {
                                    action.addAnimationGroup(targetedObiWan);
                                    action.appendTargeting(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new IntegerAwaitingDecision("Choose amount of ability to use ", 1, maxAbilityToUse, maxAbilityToUse) {
                                                        @Override
                                                        public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                            // Set secondary target filter(s)
                                                            action.addSecondaryTargetFilter(Filters.sameSiteAs(self, Filters.inActionTargetGroup(action, targetGroupId1)));
                                                            action.addSecondaryTargetFilter(Filters.battleLocation);
                                                            // Allow response(s)
                                                            action.allowResponses("Apply " + result + " of " + GameUtils.getCardLink(obiWan) + "'s ability to draw battle destiny",
                                                                    new RespondablePlayCardEffect(action) {
                                                                        @Override
                                                                        protected void performActionResults(Action targetingAction) {
                                                                            // Get the final targeted card(s)
                                                                            PhysicalCard finalObiWan = action.getPrimaryTargetCard(targetGroupId1);
                                                                            // Perform result(s)
                                                                            action.appendEffect(
                                                                                    new ApplyAbilityToDrawBattleDestinyEffect(action, playerId, finalObiWan, result));
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))) {
            final PhysicalCard battleSite = game.getGameState().getBattleLocation();
            Filter characterFilter = Filters.and(Filters.your(self), Filters.Rebel, Filters.at(Filters.adjacentSite(battleSite)),
                    Filters.canMoveAsReactAsActionFromOtherCard(self, false, GameConditions.additionalForceUseRequiredToPlayInterrupt(game, playerId, self), false));
                if (GameConditions.canTarget(game, self, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move Rebel as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Rebel", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedRebel) {
                                action.addAnimationGroup(targetedRebel);
                                // Set secondary target filter(s)
                                action.addSecondaryTargetFilter(Filters.battleLocation);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedRebel) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the final targeted card(s)
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId1);
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalCharacter, false));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}