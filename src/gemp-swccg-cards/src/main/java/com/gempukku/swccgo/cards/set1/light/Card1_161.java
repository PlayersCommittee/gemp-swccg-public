package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractSeeker;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Tarkin Seeker
 */
public class Card1_161 extends AbstractSeeker {
    public Card1_161() {
        super(Side.LIGHT, 3, "Tarkin Seeker", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Military version of a 'remote.' Programmed to stalk specific targets or secondary targets. Heat and light sensors track with fatal accuracy. Can stow away on starships.");
        setGameText("Deploys for 1 Force to an unoccupied site. Deploys and moves like an undercover spy. When present with Tarkin (or alien) of ability < 3, choose one to be immediately lost (treat as an 'all cards' situation). Seeker is also lost.");
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        Filter filter = Filters.and(Filters.or(Filters.Tarkin, Filters.alien), Filters.abilityLessThan(3), Filters.presentWith(self), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));

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
