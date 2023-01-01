package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Exposure
 */
public class Card3_124 extends AbstractLostInterrupt {
    public Card3_124() {
        super(Side.DARK, 2, Title.Exposure, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("'Artoo says the chances of survival are 725 to 1.'");
        setGameText("Use X Force during your control phase, where X = the total number of characters present or missing at exterior marker sites under 'nighttime conditions.' Those characters are lost.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.and(Filters.character, Filters.presentAt(Filters.and(Filters.exterior_marker_site, Filters.under_nighttime_conditions)), Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            final Collection<PhysicalCard> characters = Filters.filterActive(game, self, SpotOverride.INCLUDE_MISSING, filter);
            int numCharacters = characters.size();
            if (numCharacters > 0
                    && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, numCharacters)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make " + numCharacters + "characters lost");
                action.addAnimationGroup(characters);
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, numCharacters));
                // Allow response(s)
                action.allowResponses("Make " + GameUtils.getAppendedNames(characters) + " lost",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseCardsFromTableEffect(action, characters, true));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}