package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: First Order
 * Title: FN-2003
 */
public class Card204_039 extends AbstractFirstOrder {
    public Card204_039() {
        super(Side.DARK, 2, 1, 2, 1, 3, "FN-2003", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Stormtrooper.");
        setGameText("Once per turn, if with your First Order trooper, may use 1 Force to target opponent's character present (free if Finn) and make FN-2003 'hit'; for remainder of turn, target's game text is canceled and target may not fire weapons.");
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.First_Order_character, Filters.trooper))) {
            Filter filter= Filters.and(Filters.opponents(self), Filters.character, Filters.present(self));
            final Filter targetFilter = GameConditions.canUseForce(game, playerId, 1) ? filter : Filters.and(Filters.Finn, filter);
            if (GameConditions.canTarget(game, self, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Target opponent's character");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", targetFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                if (!Filters.Finn.accepts(game, targetedCard)) {
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, 1));
                                }
                                action.appendCost(
                                        new HitCardEffect(action, self, self));
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text and prevent " + GameUtils.getCardLink(targetedCard) + " from firing weapons",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotFireWeaponsModifier(self, targetedCard),
                                                                "Makes " + GameUtils.getCardLink(targetedCard) + " not fire weapons"));
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
