package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Set: A New Hope
 * Type: Effect
 * Subtype: Utinni
 * Title: Cell 2187
 */
public class Card2_030 extends AbstractUtinniEffect {
    public Card2_030() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Cell_2187, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("'Aren't you a little short for a stormtrooper?'");
        setGameText("Deploy on any Death Star site except Docking Bay 327. Target your spy or leader not on Death Star. When target reaches Utinni Effect, draw destiny. Release that many captives from the Detention Block Corridor. Lose Utinni Effect.");
        addIcons(Icon.A_NEW_HOPE);
        addKeyword(Keyword.CAN_RELEASE_CAPTIVES);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Death_Star_site, Filters.except(Filters.Docking_Bay_327));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.or(Filters.spy, Filters.leader), Filters.not(Filters.on(Title.Death_Star)));
    }

    @Override
    public Map<InactiveReason, Boolean> getTargetSpotOverride(TargetId targetId) {
        return SpotOverride.INCLUDE_UNDERCOVER;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (!GameConditions.isUtinniEffectReached(game, self)) {
                if (GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Draw destiny");
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
                            new DrawDestinyEffect(action, playerId) {
                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                    final GameState gameState = game.getGameState();
                                    if (totalDestiny == null) {
                                        gameState.sendMessage("Result: No result due to failed destiny draw");
                                        return;
                                    }

                                    gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                    if (totalDestiny >= 1) {
                                        Collection<PhysicalCard> captives = Filters.filterActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.captive, Filters.at(Filters.Detention_Block_Corridor)));
                                        if (captives.isEmpty()) {
                                            gameState.sendMessage("Result: No captives to release");
                                        }
                                        else {
                                            int count = Math.min(captives.size(), totalDestiny.intValue());
                                            gameState.sendMessage("Result: Release " + GuiUtils.formatAsString(totalDestiny) + " captive" + GameUtils.s(totalDestiny));
                                            action.insertEffect(
                                                    new ChooseCardsOnTableEffect(action, playerId, "Choose captives to release", count, count, captives) {
                                                        @Override
                                                        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                                                            action.insertEffect(
                                                                    new ReleaseCaptivesEffect(action, selectedCards));
                                                        }
                                                    });
                                        }
                                    }
                                    else {
                                        gameState.sendMessage("Result: No result");
                                    }
                                }
                            });
                    action.appendEffect(
                            new LoseCardFromTableEffect(action, self));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}