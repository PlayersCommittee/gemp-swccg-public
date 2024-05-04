package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LightsaberCombatDirections;
import com.gempukku.swccgo.logic.effects.LightsaberCombatEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Objective
 * Title: We'll Handle This / Duel Of The Fates
 */
public class Card13_046_BACK extends AbstractObjective {
    public Card13_046_BACK() {
        super(Side.LIGHT, 7, Title.Duel_Of_The_Fates, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setGameText("While this side up, you may not Force drain or initiate battle at any location where you have a Jedi.  Once during your move phase, your Jedi may initiate lightsaber combat against an opponent's Dark Jedi present: Each player draws 2 destiny.  Loser (lowest total) loses 2 Force (cannot be reduced).  If difference is 5 or greater, lost Force must come from Reserve Deck, and losing character is lost. Flip this card and retrieve 1 Force if opponent has no Dark Jedi present at any interior Naboo battleground.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameLocationAsYourJedi = Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Jedi));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, sameLocationAsYourJedi, playerId));
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, sameLocationAsYourJedi, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        Filter jediFilter = Filters.and(Filters.your(self), Filters.Jedi, Filters.at(Filters.wherePresent(self, Filters.and(Filters.opponents(self), Filters.Dark_Jedi, Filters.canBeTargetedBy(self)))));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.canTarget(game, self, jediFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Initiate lightsaber combat");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", jediFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard jedi) {
                            Filter darkJediFilter = Filters.and(Filters.opponents(self), Filters.Dark_Jedi, Filters.present(jedi));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", darkJediFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, final PhysicalCard darkJedi) {
                                            action.addAnimationGroup(jedi, darkJedi);
                                            // Allow response(s)
                                            action.allowResponses("Initiate lightsaber combat between " + GameUtils.getCardLink(jedi) + " and " + GameUtils.getCardLink(darkJedi),
                                                    new UnrespondableEffect(action) {
                                                        @Override
                                                        protected void performActionResults(final Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new LightsaberCombatEffect(action, darkJedi, jedi, new LightsaberCombatDirections() {
                                                                        @Override
                                                                        public Evaluator getBaseLightsaberCombatTotal(final String playerId, final LightsaberCombatState lightsaberCombatState) {
                                                                            return new ConstantEvaluator(0);
                                                                        }

                                                                        @Override
                                                                        public int getBaseNumLightsaberCombatDestinyDraws(String playerId, LightsaberCombatState lightsaberCombatState) {
                                                                            return 2;
                                                                        }

                                                                        @Override
                                                                        public void performLightsaberCombatDirections(final Action lightsaberCombatAction, SwccgGame game, final LightsaberCombatState lightsaberCombatState) {
                                                                            lightsaberCombatAction.appendEffect(
                                                                                    new DrawDestinyEffect(lightsaberCombatAction, game.getLightPlayer(), game.getModifiersQuerying().getNumLightsaberCombatDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.LIGHTSABER_COMBAT_DESTINY) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                                                            if (lightTotalDestiny != null) {
                                                                                                lightsaberCombatState.increaseTotalLightsaberCombatDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                                                            }
                                                                                            lightsaberCombatAction.appendEffect(
                                                                                                    new DrawDestinyEffect(lightsaberCombatAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumLightsaberCombatDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.LIGHTSABER_COMBAT_DESTINY) {
                                                                                                        @Override
                                                                                                        protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                                                                            if (darkTotalDestiny != null) {
                                                                                                                lightsaberCombatState.increaseTotalLightsaberCombatDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                            );
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }

                                                                        @Override
                                                                        public void performLightsaberCombatResults(Action lightsaberCombatAction, SwccgGame game, LightsaberCombatState lightsaberCombatState) {
                                                                            String losingPlayer = lightsaberCombatState.getLoser();
                                                                            if (losingPlayer != null) {
                                                                                String winningPlayer = lightsaberCombatState.getWinner();
                                                                                float forceLoss = game.getModifiersQuerying().getLightsaberCombatForceLoss(game.getGameState(), 2);
                                                                                if ((lightsaberCombatState.getFinalLightsaberCombatTotal(winningPlayer) - lightsaberCombatState.getFinalLightsaberCombatTotal(losingPlayer)) >= 5) {
                                                                                    // Lose Force from Reserve Deck (if possible)
                                                                                    lightsaberCombatAction.appendEffect(
                                                                                            new LoseForceFromReserveDeckEffect(action, losingPlayer, forceLoss, true));
                                                                                    PhysicalCard losingCharacter = lightsaberCombatState.getLosingCharacter();
                                                                                    if (losingCharacter != null) {
                                                                                        // Losing character is lost
                                                                                        lightsaberCombatAction.appendEffect(
                                                                                                new LoseCardFromTableEffect(lightsaberCombatAction, losingCharacter));
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    // Lose Force
                                                                                    lightsaberCombatAction.appendEffect(
                                                                                            new LoseForceEffect(action, losingPlayer, forceLoss, true));
                                                                                }
                                                                            }
                                                                        }
                                                                    }));
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self),
                Filters.Dark_Jedi, Filters.presentAt(Filters.and(Filters.interior_Naboo_site, Filters.battleground_site))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip and retrieve 1 Force");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}