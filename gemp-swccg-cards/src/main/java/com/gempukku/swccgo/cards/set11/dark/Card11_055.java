package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalAbilityModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Sith
 * Title: Darth Maul (AI)
 */
public class Card11_055 extends AbstractSith {
    public Card11_055() {
        super(Side.DARK, 1, 8, 7, 6, 8, "Darth Maul", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.R);
        setAlternateImageSuffix(true);
        setLore("Apprentice to Darth Sidious. Sent to capture Queen Amidala on Tatooine. Full of anger and fury as a child. Sidious used Maul's pent-up rage to train him in the ways of the Sith.");
        setGameText("Deploys -2 to Tatooine. During your control phase may lose 1 Force to duel opponent's Jedi present. Both players draw 2 destiny. Loser is lost. Unless opponent's character of ability > 3 here, opponent's total ability here = 0. Immune to attrition < 5.");
        addPersona(Persona.MAUL);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Tatooine));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.present(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_DUELED;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Duel a Jedi");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            // Allow response(s)
                            action.allowResponses("Duel " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DuelEffect(action, self, targetedCard, new DuelDirections() {
                                                        @Override
                                                        public boolean isEpicDuel() {
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean isCrossOverToDarkSideAttempt() {
                                                            return false;
                                                        }

                                                        @Override
                                                        public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                                                            return new ConstantEvaluator(0);
                                                        }

                                                        @Override
                                                        public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                                            return 2;
                                                        }

                                                        @Override
                                                        public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                                            duelAction.appendEffect(
                                                                    new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                                            if (darkTotalDestiny != null) {
                                                                                duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                                            }
                                                                            duelAction.appendEffect(
                                                                                    new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                                                            if (lightTotalDestiny != null) {
                                                                                                duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }
                                                                    }
                                                            );
                                                        }

                                                        @Override
                                                        public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                                            PhysicalCard losingCharacter = duelState.getLosingCharacter();
                                                            if (losingCharacter != null) {
                                                                // Losing character is lost
                                                                duelAction.appendEffect(
                                                                        new LoseCardFromTableEffect(duelAction, losingCharacter));
                                                            }
                                                        }
                                                    }));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetTotalAbilityModifier(self, Filters.here(self),
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.opponents(self), Filters.character,
                        Filters.abilityMoreThan(3)))), 0, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
