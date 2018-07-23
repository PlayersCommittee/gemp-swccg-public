package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Mustafar: Vader's Castle
 */
public class Card209_050 extends AbstractSite {
    public Card209_050() {
        super(Side.DARK, Title.Vaders_Castle, Title.Mustafar);
        setLocationDarkSideGameText("Once per game, may \\/ Vader here. During your move phase, Vader may move between here and any battleground site.");
        setLocationLightSideGameText("");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.VIRTUAL_SET_9, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter otherBattlegroundSites = Filters.and(Filters.other(self), Filters.battleground_site);

        // Both the Move Vader and Deploy Vader functionality return an action" object, but I think each time thru here
        //  it is only possible for one to be returned.  (Because they're in separate phases)

        // Move Vader
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.MOVE)
                && GameConditions.canSpotLocation(game, otherBattlegroundSites)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.Vader, self, otherBattlegroundSites, false)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.Vader, self, otherBattlegroundSites, false);
                action.setText("Move from here to other battleground site");
                actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerOnDarkSideOfLocation, game, Filters.Vader, otherBattlegroundSites, self, false)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerOnDarkSideOfLocation, game, self, gameTextSourceCardId, Filters.Vader, otherBattlegroundSites, self, false);
                action.setText("Move from other battleground site to here");
                actions.add(action);
            }
        }


        // Deploy Vader
        GameTextActionId gameTextActionId = GameTextActionId.VADERS_CASTLE__DOWNLOAD_VADER;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerOnDarkSideOfLocation, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Vader from Reserve Deck");
            action.setActionMsg("Deploy Vader from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Vader, Filters.here(self), true));
            return Collections.singletonList(action);
        }

        return actions;
    }

}