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
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
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
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard) + " so you may add 2 to destiny totals targeting its armor or maneuver",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            final int permCardId = self.getPermanentCardId();
                                            final int targetPermCardId = finalTarget.getPermanentCardId();
                                            final int gameTextSourceCardId = self.getCardId();
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);
                                                                    final PhysicalCard target = game.findCardByPermanentId(targetPermCardId);
                                                                    if (playerId2.equals(playerId)
                                                                            && target != null
                                                                            && TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, target)) {
                                                                        final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                                                                        action2.setText("Add 2 to destiny total");
                                                                        action2.appendEffect(
                                                                                new ModifyDestinyEffect(action2, 2));
                                                                        actions.add(action2);
                                                                    }
                                                                    return actions;
                                                                }
                                                            }));
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
