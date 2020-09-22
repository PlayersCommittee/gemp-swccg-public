package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: A Sith Legend
 */
public class Card501_013 extends AbstractUsedOrLostInterrupt {
    public Card501_013() {
        super(Side.DARK, 2, "A Sith Legend", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("");
        setGameText("USED: Deploy a lightsaber (may simultaneously deploy a matching Dark Jedi or Sith character) from hand and/or Reserve Deck; reshuffle. [Immune to Sense.] LOST: Except during battle, relocate a Dark Jedi or Inquisitor to same battleground site as a Jedi.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        setTestingText("A Sith Legend");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId exchangeCardActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)

        Filter jediAtBattlegroundSites = Filters.and(Filters.Jedi, Filters.at(Filters.battleground_site));
        final Filter battlegroundSitesJediAreAt = Filters.sameSiteAs(self, jediAtBattlegroundSites);
        Filter darkJediOrInquisitor = Filters.or(Filters.Dark_Jedi, Filters.inquisitor);
        Filter darkJediOrInquisitorWhoCanBeRelocated = Filters.and(darkJediOrInquisitor, Filters.canBeRelocatedToLocation(battlegroundSitesJediAreAt, 0));

        if (!GameConditions.isDuringBattle(game)
            && GameConditions.canSpot(game, self, darkJediOrInquisitorWhoCanBeRelocated)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, exchangeCardActionId, CardSubtype.LOST);
            action.setText("Relocate Dark Jedi or Inquisitor");
            action.setActionMsg("Relocate a Dark Jedi or Inquisitor to same battleground site as a Jedi.");

            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi or Inquisitor", darkJediOrInquisitorWhoCanBeRelocated) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard characterTargeted) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, self.getOwner(), "Choose site to relocate character", battlegroundSitesJediAreAt) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard siteSelected) {
                                            action.addAnimationGroup(characterTargeted);
                                            action.addAnimationGroup(siteSelected);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, siteSelected, 0));
                                            // Allow response(s)
                                            action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, characterTargeted, siteSelected));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}