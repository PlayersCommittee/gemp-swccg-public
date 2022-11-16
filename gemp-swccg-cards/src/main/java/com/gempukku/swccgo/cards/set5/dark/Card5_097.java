package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Droid
 * Title: E-3PO
 */
public class Card5_097 extends AbstractDroid {
    public Card5_097() {
        super(Side.DARK, 3, 2, 1, 3, "E-3PO", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Brought to Cloud City by the Empire. Facilitates Imperial-to-alien communication. Relayed carbonite chamber modification instructions to Ugloste. Arrogant. Rude. Vulgar.");
        setGameText("Total power at same site is +1 for each of your alien/Imperial pairs present. Once during every turn, may use 1 Force to 'insult' one opponent's droid present (except R2-D2), canceling that droid's game text for remainder of turn.");
        addModelType(ModelType.PROTOCOL);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self),
                new MinEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.alien)),
                        new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.Imperial))), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter targetFilter = Filters.and(Filters.opponents(playerId), Filters.droid, Filters.except(Filters.R2D2), Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Insult' an opponent's droid");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose droid", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("'Insult' " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
