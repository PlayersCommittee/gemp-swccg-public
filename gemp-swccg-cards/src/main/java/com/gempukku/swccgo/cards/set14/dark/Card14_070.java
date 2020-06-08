package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: 3B3-1204
 */
public class Card14_070 extends AbstractDroid {
    public Card14_070() {
        super(Side.DARK, 2, 3, 2, 3, "3B3-1204", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Infantry battle droid programmed with anti-Jedi combat tactics by the blockade flagship. Was to begin uploading his program to the rest of the unit when the Jedi escaped.");
        setGameText("While with two other battle droids, may use 1 Force to target an opponent's Jedi present. Target's immunity to attrition is canceled for remainder of turn.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.INFANTRY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.hasAnyImmunityToAttrition, Filters.present(self));

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.isWith(game, self, 2, Filters.and(Filters.other(self), Filters.battle_droid))
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel a Jedi's immunity to attrition");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s immunity to attrition",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, targetedCard,
                                                            "Cancels " + GameUtils.getCardLink(targetedCard) + "'s immunity to attrition"));
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
