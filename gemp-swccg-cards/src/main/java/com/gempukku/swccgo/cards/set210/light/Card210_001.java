package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Location
 * Subtype: Site
 * Title: Ahch-To: Saddle
 */
public class Card210_001 extends AbstractSite {
    public Card210_001() {
        super(Side.LIGHT, Title.Saddle, Title.Ahch_To);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("May [download] [Episode VII] Luke here. Once per turn, if Luke alone here, may subtract 2 from opponent's total power during a battle at another location.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.EPISODE_VII, Icon.VIRTUAL_SET_10);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.AHCH_TO__DOWNLOAD_LUKE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Persona.LUKE)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Episode VII] Luke from Reserve Deck");
            action.setActionMsg("Deploy [Episode VII] Luke from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_VII, Filters.Luke), Filters.here(self), true));
            actions.add(action);
        }

        Filter lukeAloneHere = Filters.and(Filters.Luke, Filters.alone, Filters.here(self));
        gameTextActionId = GameTextActionId.AHCH_TO__SUBTRACT_FROM_POWER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.not(self))
                && GameConditions.canSpot(game, self, lukeAloneHere)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Reduce opponent's total power by 2");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new SubtractFromOpponentsTotalPowerEffect(action, 2));
            actions.add(action);
        }

        return actions;
    }
}