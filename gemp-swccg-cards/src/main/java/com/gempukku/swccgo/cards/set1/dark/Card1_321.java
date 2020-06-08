package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSeeker;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Weapon
 * Subtype: Automated
 * Title: Luke Seeker
 */
public class Card1_321 extends AbstractSeeker {
    public Card1_321() {
        super(Side.DARK, 3, "Luke Seeker");
        setLore("Military version of a 'remote.' Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Use 1 Force to deploy on opponent's side at any unoccupied site. Moves during your control phase, like a character, at normal use of the Force. When at same location as Luke of ability < 4 or pilot of ability < 3, choose one to be immediately lost. Seeker also lost.");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter filter = Filters.and(Filters.or(Filters.and(Filters.Luke, Filters.abilityLessThan(4)), Filters.and(Filters.pilot, Filters.abilityLessThan(3))), Filters.presentWith(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, filter)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make a character lost");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to make lost", filter) {
                        @Override
                        protected void cardSelected(final PhysicalCard character) {
                            action.addAnimationGroup(character);
                            action.setActionMsg("Make " + GameUtils.getCardLink(character) + " lost");
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseCardsFromTableSimultaneouslyEffect(action, Arrays.asList(character, self), true, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
