package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Get Them Out Of My Sight
 */
public class Card305_034 extends AbstractUsedOrLostInterrupt {
    public Card305_034() {
        super(Side.DARK, 4, Title.Get_Them_Out_Of_My_Sight, Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.C);
        setLore("During the occupation of Quermia, battle droids were ordered to clear the population out of major civil centers which resulted in several massacres.");
        setGameText("USED: Subtract one from a just drawn battle destiny. LOST: During opponent's control phase, use X Force to 'break cover' of an opponent's undercover spy at same site as your battle droid or Sephi, where X = spy's deploy cost.");
        addIcons(Icon.ABT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Subtract 1 from battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, -1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.CONTROL)) {
            int forceAvailableToUse = GameConditions.forceAvailableToUseToPlayInterrupt(game, playerId, self);
            Filter targetFilter = Filters.and(Filters.opponents(self), Filters.undercover_spy, Filters.deployCostLessThanOrEqualTo(forceAvailableToUse),
                    Filters.at(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.battle_droid, Filters.Sephi)))));
            if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_UNDERCOVER, targetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Break a spy's cover");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose undercover spy", SpotOverride.INCLUDE_UNDERCOVER, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                float deployCost = game.getModifiersQuerying().getDeployCost(game.getGameState(), cardTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, deployCost));
                                // Allow response(s)
                                action.allowResponses("'Break cover' of " + GameUtils.getCardLink(cardTargeted),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalSpy = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new BreakCoverEffect(action, finalSpy));
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