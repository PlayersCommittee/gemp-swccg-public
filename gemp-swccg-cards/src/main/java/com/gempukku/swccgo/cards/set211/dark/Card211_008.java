package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Device
 * Title: Carbonite Chamber Console (V)
 */
public class Card211_008 extends AbstractDevice {
    public Card211_008() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Carbonite_Chamber_Console, Uniqueness.UNIQUE);
        setLore("Most often used to freeze Tibanna gas for transport. Modified by Ugloste to work on humans. Intended to capture Luke Skywalker, the Emperor's prize.");
        setGameText("Deploy on Carbonite Chamber. Once during opponent's turn, if a frozen captive on table, may activate 1 Force. Once per turn, may use 1 Force to [upload] He's All Yours, Bounty Hunter.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_11);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Carbonite_Chamber;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.frozenCaptive)
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.CARBONITE_CHAMBER_CONSOLE__UPLOAD_HES_ALL_YOURS_BOUNTY_HUNTER;

        if(GameConditions.isOncePerTurn(game, self, playerId,gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)){

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use 1 Force to take He's All Yours, Bounty Hunter into hand from Reserve Deck");
            action.setActionMsg("Take He's All Yours, Bounty Hunter into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Hes_All_Yours_Bounty_Hunter), true));
            actions.add(action);
        }

        return actions;
    }
}
