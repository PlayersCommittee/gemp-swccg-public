package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedAtEndOfAttackRunResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Tight Squeeze
 */
public class Card4_065 extends AbstractLostInterrupt {
    public Card4_065() {
        super(Side.LIGHT, 3, Title.Tight_Squeeze, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Rebel pilots often join the Rebellion from Outer Rim worlds. Bush pilots fly by the seat of their pants, giving them a huge advantage in close quarters.");
        setGameText("If you have a lone vehicle or starfighter in a battle or Attack Run at Beggar's Canyon, Cloud City, Death Star: Trench or an asteroid sector, use 1 Force. At the end of that battle or Attack Run, opponent must forfeit two participating vehicles or starfighters.");
        addIcons(Icon.DAGOBAH);
    }

    private Filter getValidLocationFilter() {
        return Filters.or(Filters.Beggars_Canyon, Filters.Bespin_Cloud_City, Filters.Death_Star_Trench, Filters.asteroid_sector);
    }

    private Filter getYourLoneVehicleOrStarfighterFilter(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starfighter), Filters.alone);
    }

    private Filter getOpponentsParticipatingVehicleOrStarfighterFilter(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.vehicle, Filters.starfighter), Filters.participatingInBattle);
    }

    private boolean canPlayDuringBattle(SwccgGame game, PhysicalCard self, String playerId) {
        return GameConditions.isDuringBattleAt(game, getValidLocationFilter())
                && GameConditions.isDuringBattleWithParticipant(game, getYourLoneVehicleOrStarfighterFilter(self))
                && GameConditions.canSpot(game, self, 2, getOpponentsParticipatingVehicleOrStarfighterFilter(self))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1);
    }

    private boolean canPlayDuringAttackRun(SwccgGame game, PhysicalCard self, String playerId) {
        Filter yourLone = Filters.and(getYourLoneVehicleOrStarfighterFilter(self), Filters.at(Filters.Death_Star_Trench));
        Filter opponentsAtTrench = Filters.and(Filters.opponents(self), Filters.or(Filters.vehicle, Filters.starfighter), Filters.at(Filters.Death_Star_Trench));
        return GameConditions.isDuringAttackRunWithParticipant(game, yourLone)
                && GameConditions.canSpot(game, self, 2, opponentsAtTrench)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1);
    }

    private void appendForfeitTwoEffect(final Action action, final SwccgGame game, final PhysicalCard self, final String opponent, final Filter targetFilter) {
        int available = Filters.countActive(game, self, targetFilter);
        int num = Math.min(2, available);
        if (num <= 0) {
            return;
        }
        action.appendEffect(
                new ChooseCardsOnTableEffect(action, opponent, "Choose vehicle or starfighter to forfeit", num, num, targetFilter) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        for (PhysicalCard selected : selectedCards) {
                            action.appendEffect(new ForfeitCardFromTableEffect(action, selected));
                        }
                    }
                }
        );
    }

    private PlayInterruptAction createBattleAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Force opponent to forfeit two at end of battle");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.allowResponses("At end of battle, opponent forfeits two participating vehicles or starfighters",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        final int permCardId = self.getPermanentCardId();
                        final int gameTextSourceCardId = self.getCardId();
                        action.appendEffect(
                                new AddUntilEndOfBattleActionProxyEffect(action,
                                        new AbstractActionProxy() {
                                            @Override
                                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                final PhysicalCard self = game.findCardByPermanentId(permCardId);
                                                if (TriggerConditions.battleEndingAt(game, effectResult, getValidLocationFilter())) {
                                                    final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                    action2.setText("Opponent forfeits two vehicles or starfighters");
                                                    appendForfeitTwoEffect(action2, game, self, opponent, getOpponentsParticipatingVehicleOrStarfighterFilter(self));
                                                    return Collections.singletonList((TriggerAction) action2);
                                                }
                                                return null;
                                            }
                                        }
                                )
                        );
                    }
                }
        );
        return action;
    }

    private PlayInterruptAction createAttackRunAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Force opponent to forfeit two at end of Attack Run");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.allowResponses("At end of Attack Run, opponent forfeits two participating vehicles or starfighters",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        final int permCardId = self.getPermanentCardId();
                        final int gameTextSourceCardId = self.getCardId();
                        action.appendEffect(
                                new AddUntilEndOfTurnActionProxyEffect(action,
                                        new AbstractActionProxy() {
                                            private boolean _fired;

                                            @Override
                                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                if (_fired) {
                                                    return null;
                                                }
                                                final PhysicalCard self = game.findCardByPermanentId(permCardId);
                                                if (effectResult instanceof MovedAtEndOfAttackRunResult) {
                                                    _fired = true;
                                                    final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                    action2.setText("Opponent forfeits two vehicles or starfighters");
                                                    Filter forfeitFilter = Filters.and(Filters.opponents(self), Filters.or(Filters.vehicle, Filters.starfighter), Filters.at(Filters.Death_Star_Trench));
                                                    appendForfeitTwoEffect(action2, game, self, opponent, forfeitFilter);
                                                    return Collections.singletonList((TriggerAction) action2);
                                                }
                                                return null;
                                            }
                                        }
                                )
                        );
                    }
                }
        );
        return action;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (canPlayDuringBattle(game, self, playerId)) {
            actions.add(createBattleAction(playerId, game, self));
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelAttackRunActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (canPlayDuringAttackRun(game, self, playerId)) {
            return Collections.singletonList(createAttackRunAction(playerId, game, self));
        }
        return null;
    }
}
