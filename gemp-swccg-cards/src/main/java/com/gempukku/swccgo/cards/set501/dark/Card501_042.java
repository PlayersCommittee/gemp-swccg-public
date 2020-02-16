package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Card501_042 extends AbstractStartingInterrupt {
    public Card501_042() {
        super(Side.DARK, 3, "Slip Sliding Away", Uniqueness.UNIQUE);
        setLore("Luke got the shaft.");
        setVirtualSuffix(true);
        setGameText("If you deployed exactly one location (and it was a site with exactly 2 [DS]), deploy one battleground site. If you did not deploy a site with \"Palace\" in title, you may also deploy up to 3 Effects that are always immune to Alter. Place Interrupt in Lost pile.");
        addIcons(Icon.VIRTUAL_SET_12);
        setTestingText("Slip Sliding Away v");
    }
    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        final Filter startingLocation = Filters.and(Filters.owner(playerId),
                Filters.and(Filters.iconCount(Icon.DARK_FORCE, 2),
                        CardSubtype.SITE));
        action.setText("Deploy one battleground site. If you did not deploy a site with \"Palace\" in title, you may also deploy up to 3 Effects that are always immune to Alter. Place Interrupt in Lost pile.");
        if (GameConditions.canSpotLocation(game, 1, Filters.owner(playerId)) &&
                GameConditions.canSpotLocation(game, startingLocation)) {
            // Allow response(s)
            action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendTargeting(
                            new ChooseCardFromReserveDeckEffect(action, playerId, Filters.battleground_site) {
                                @Override
                                public String getChoiceText (int numCardsToChoose){
                                    return "Choose battleground site";
                                }

                                @Override
                                protected void cardSelected (SwccgGame game, final PhysicalCard selectedSite){
                                    if (!selectedSite.getBlueprint().getTitle().toLowerCase().contains("palace")) { //should change this to Filters.titleContains("Palace");
                                        action.appendEffect(new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 3, true, false));
                                    }
                                    action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.sameCardId(selectedSite), false));
                                }
                            }
                        );
                        action.appendEffect(
                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                }
            );
        }
        return action;
    }
}