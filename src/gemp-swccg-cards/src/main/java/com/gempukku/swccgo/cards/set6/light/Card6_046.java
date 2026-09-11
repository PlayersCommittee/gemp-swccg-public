package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Yarkora
 */
public class Card6_046 extends AbstractAlien {
    public Card6_046() {
        super(Side.LIGHT, 3, 2, 1, 1, 2, "Yarkora", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Mysterious, secretive aliens. Tend to be found as couriers, scouts and t'bac farmers. Some have helped the Alliance's efforts at counter-espionage.");
        setGameText("If at same site as an Undercover spy during your control phase, may draw destiny. Each of your Yarkoras on table may cumulatively subtract one from that destiny. Spy's 'cover is broken' if destiny = spy's ability.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.YARKORA);
    }

    /**
     * Active Yarkoras owned by the player that may optionally subtract from this destiny draw.
     * Excludes inactive (e.g. missing) and supporting cards via Filters.filterActive.
     */
    static Collection<PhysicalCard> YarkoraGetActiveOwnedYarkoras(SwccgGame game, String playerId) {
        return Filters.filterActive(game, null, Filters.and(Filters.owner(playerId), Filters.Yarkora));
    }

    /**
     * Action proxy granting each active owned Yarkora one optional -1 to this specific destiny draw.
     * Species-based (not gametext), so Saelt-Marae / canceled gametext still eligible.
     */
    static ActionProxy YarkoraCreateSubtractActionProxy(final String playerId, final DrawDestinyState drawDestinyState) {
        return new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                if (!playerId2.equals(playerId)
                        || !TriggerConditions.isDestinyJustDrawn(game, effectResult, drawDestinyState)) {
                    return actions;
                }
                for (PhysicalCard yarkora : YarkoraGetActiveOwnedYarkoras(game, playerId)) {
                    final OptionalGameTextTriggerAction subtractAction =
                            new OptionalGameTextTriggerAction(yarkora, playerId, yarkora.getCardId());
                    subtractAction.setText("Subtract 1 from destiny (" + GameUtils.getFullName(yarkora) + ")");
                    subtractAction.setActionMsg("Subtract 1 from destiny using " + GameUtils.getCardLink(yarkora));
                    // cumulative=true so multiple copies titled Yarkora each stack -1
                    subtractAction.appendEffect(
                            new ModifyDestinyEffect(subtractAction, -1, true));
                    actions.add(subtractAction);
                }
                return actions;
            }
        };
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy, Filters.atSameSite(self));
        Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_UNDERCOVER;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, spotOverride, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Break a spy's cover");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target undercover spy", spotOverride, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("'Break cover' of " + GameUtils.getCardLink(cardTargeted),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(cardTargeted);
                                                        }

                                                        @Override
                                                        protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, final DrawDestinyState drawDestinyState) {
                                                            return Collections.singletonList(
                                                                    YarkoraCreateSubtractActionProxy(playerId, drawDestinyState));
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), cardTargeted);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));

                                                            if (totalDestiny == ability) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new BreakCoverEffect(action, cardTargeted));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
