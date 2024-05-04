package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Effect
 * Title: Underworld Contacts
 */
public class Card112_010 extends AbstractNormalEffect {
    public Card112_010() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Underworld_Contacts, Uniqueness.UNIQUE, ExpansionSet.JPSD, Rarity.PM);
        setLore("Influence, like any commodity, has a price in the Outer Rim territories.");
        setGameText("Deploy on a Tatooine site. Your Force generation here is +1. Opponent may not cancel or modify Force drains at each Tatooine battleground site where you have two aliens with different card titles. At any time, you may use 2 Force to raise your converted Tatooine site to top.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Tatooine_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter tatooineBattlegroundSiteWithAliensWithDiffCardTitles = Filters.and(Filters.Tatooine_site, Filters.battleground_site,
                Filters.hasDifferentCardTitlesAtLocation(self, Filters.and(Filters.your(self), Filters.alien)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, tatooineBattlegroundSiteWithAliensWithDiffCardTitles, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, tatooineBattlegroundSiteWithAliensWithDiffCardTitles, opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.Tatooine_site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Raise a converted Tatooine site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose site to convert", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
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