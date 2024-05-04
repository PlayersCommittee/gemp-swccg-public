package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Aiiii! Aaa! Agggggggggg!
 */
public class Card5_130 extends AbstractLostInterrupt {
    public Card5_130() {
        super(Side.DARK, 4, Title.Aiiii_Aaa_Agggggggggg, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("'They never even asked me any questions.'");
        setGameText("If you have a Rebel of ability > 1 captive at a prison you control, opponent loses X Force, where X = the number of opponent's characters with ability > 2 on table.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        TargetingReason targetingReason = TargetingReason.TO_BE_TORTURED;
        Filter prisonYouControl = Filters.and(Filters.prison, Filters.controls(playerId));
        Filter captiveFilter = Filters.and(Filters.Rebel, Filters.abilityMoreThan(1), Filters.captive, Filters.or(Filters.at(prisonYouControl), Filters.imprisonedIn(prisonYouControl)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, targetingReason, captiveFilter)) {
            final int amountToForce = Filters.countActive(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(2)));
            if (amountToForce > 0) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent lose " + amountToForce + " Force");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, targetingReason, captiveFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard captive) {
                                action.addAnimationGroup(captive);
                                // Allow response(s)
                                action.allowResponses("Make opponent lose " + amountToForce + " Force by torturing " + GameUtils.getCardLink(captive),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseForceEffect(action, opponent, amountToForce));
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