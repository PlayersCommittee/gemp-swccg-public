package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.SearchPartyAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Hunting Party
 */
public class Card7_252 extends AbstractUsedInterrupt {
    public Card7_252() {
        super(Side.DARK, 7, "Hunting Party", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Sometimes a missing person is found by the wrong search party.");
        setGameText("During your control phase, form a search party for an opponent's missing character at same site. Add 1 to search party destiny draw for each bounty hunter in search party. If successful, capture the character found.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            String opponent = game.getOpponent(playerId);
            Collection<PhysicalCard> sites = Filters.filterTopLocationsOnTable(game,Filters.site);

            List<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
            for (PhysicalCard site : sites) {
                if ((Filters.canSpot(game, self, SpotOverride.INCLUDE_MISSING_AND_UNDERCOVER, Filters.and(Filters.owner(opponent), Filters.missing, Filters.at(site)))) &&
                        Filters.canSpot(game, self, Filters.canJoinSearchPartyAt(playerId, site))) validSites.add(site);
            }
            if (!validSites.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Form a search party");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose site", Filters.in(validSites)) {
                            @Override
                            protected void cardTargeted(int siteTargetGroupId, PhysicalCard site) {
                                // Allow response(s)
                                action.allowResponses("Form a search party at " + GameUtils.getCardLink(site),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PassthruEffect(action) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                PhysicalCard finalSite = action.getPrimaryTargetCard(siteTargetGroupId);
                                                                action.appendEffect(
                                                                        new StackActionEffect(action, new SearchPartyAction(playerId, finalSite, true) {
                                                                            //SearchPartyAction is normally top level action with null source, so must overload
                                                                            @Override
                                                                            public PhysicalCard getActionSource() {
                                                                                return self;
                                                                            }
                                                                        }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                );
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
