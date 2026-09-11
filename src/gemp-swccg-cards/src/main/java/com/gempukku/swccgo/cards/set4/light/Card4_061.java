package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.TotalAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: They'd Be Crazy To Follow Us
 */
public class Card4_061 extends AbstractUsedInterrupt {
    public Card4_061() {
        super(Side.LIGHT, 4, Title.Theyd_Be_Crazy_To_Follow_Us, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Flying into an asteroid field is considered to be certain death except by Han Solo, Rycar Ryjerd and the terminally insane.");
        setGameText("Use 1 Force to target a starship at an asteroid sector or a \"blown away\" system. For remainder of turn, you may add 2 to destiny totals targeting the armor or maneuver of that starship.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter locationFilter = Filters.or(Filters.asteroid_sector, Filters.and(Filters.system, Filters.blown_away));
        final Filter targetFilter = Filters.and(Filters.starship, Filters.at(locationFilter));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a starship");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Add 2 to destiny totals targeting armor or maneuver of " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new TotalAsteroidDestinyModifier(self, 2, finalTarget),
                                                            "Adds 2 to destiny totals targeting armor or maneuver of " + GameUtils.getCardLink(finalTarget)));
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