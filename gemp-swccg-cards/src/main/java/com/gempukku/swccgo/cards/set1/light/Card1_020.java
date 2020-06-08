package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardToLocationEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Momaw Nadon
 */
public class Card1_020 extends AbstractAlien {
    public Card1_020() {
        super(Side.LIGHT, 3, 2, 1, 3, 3, Title.Momaw_Nadon, Uniqueness.UNIQUE);
        setLore("Male Ithorian, from a tree-loving race derogatorily nicknamed, 'Hammerheads.' Master of animal husbandry and horticulture. Former herd ship leader. Rebel spy.");
        setGameText("During your control phase, may snare (steal) one Bantha, Dewback, Wampa, Rancor, Bubo, or Dragonsnake present at same site.");
        addKeywords(Keyword.SPY);
        setSpecies(Species.ITHORIAN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.or(Filters.bantha, Filters.Dewback, Filters.wampa, Filters.Rancor, Filters.Bubo, Filters.Dragonsnake), Filters.present(self), Filters.atSameSite(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Snare' creature");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose creature to 'snare'", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            // Allow response(s)
                            action.allowResponses("'Snare' " + GameUtils.getCardLink(cardTargeted),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard cardToSteal = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new StealCardToLocationEffect(action, cardToSteal));
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
