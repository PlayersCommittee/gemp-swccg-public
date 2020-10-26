package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Gragra
 */
public class Card11_058 extends AbstractAlien {
    public Card11_058() {
        super(Side.DARK, 2, 1, 2, 1, 2, "Gragra", Uniqueness.UNIQUE);
        setLore("Female Swokes Swokes. Lives in the city of Mos Espa and runs a street-corner market there. Seller of chuba.");
        setGameText("Once per turn, may use 1 Force to target opponent's card with ability just deployed to same site. Opponent must use 2 Force or return target to hand. Power +2 at Mos Espa.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.SWOKES_SWOKES);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Mos_Espa), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility), Filters.sameSite(self))) {
            final PhysicalCard cardJustDeployed = ((PlayCardResult) effectResult).getPlayedCard();
            if (GameConditions.canTarget(game, self, cardJustDeployed)
                    && GameConditions.canUseForce(game, playerId, 1)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Return " + GameUtils.getFullName(cardJustDeployed) + " to hand");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose card", cardJustDeployed) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Update usage limit(s)
                                action.appendUsage(
                                        new OncePerTurnEffect(action));
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Return " + GameUtils.getCardLink(targetedCard) + " to opponent's hand",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(final Action targetingAction) {
                                                final String opponent = game.getOpponent(playerId);
                                                // Perform result(s)
                                                if (GameConditions.canUseForce(game, opponent, 2)) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new PlayoutDecisionEffect(action, opponent,
                                                                    new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Return " + GameUtils.getFullName(cardJustDeployed) + " to hand"}) {
                                                                        @Override
                                                                        protected void validDecisionMade(int index, String result) {
                                                                            if (index == 0) {
                                                                                game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                                                action.appendEffect(
                                                                                        new UseForceEffect(action, opponent, 2));
                                                                            } else {
                                                                                game.getGameState().sendMessage(opponent + " chooses to return " + GameUtils.getCardLink(cardJustDeployed) + " to hand");
                                                                                action.appendEffect(
                                                                                        new ReturnCardToHandFromTableEffect(action, cardJustDeployed));
                                                                            }
                                                                        }
                                                                    }
                                                            )
                                                    );
                                                } else {
                                                    action.appendEffect(
                                                            new ReturnCardToHandFromTableEffect(action, cardJustDeployed));
                                                }
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
