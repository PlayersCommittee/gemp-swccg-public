package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Effect
 * Subtype: Effect
 * Title: Echo Base Destroyed
 */

public class Card223_010 extends AbstractNormalEffect {
    public Card223_010() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Echo Base Destroyed", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setGameText("Deploy on table. Once per turn, if Main Power Generators on table, may place a card from hand under Reserve Deck to activate 1 Force; reshuffle. Once per turn, if 1st Marker 'blown away,' may take any one [Hoth] card into hand from Force Pile; reshuffle. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_23, Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.Main_Power_Generators)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canActivateForce(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand under Reserve Deck");
            action.setActionMsg("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            action.appendCost(
                    new PutCardFromHandOnBottomOfReserveDeckEffect(action, playerId));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action));

            actions.add(action);
        }

        gameTextActionId = GameTextActionId.ECHO_BASE_DESTROYED__TAKE_CARD_INTO_HAND_FROM_FORCE_PILE;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isBlownAway(game, Filters.First_Marker)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take [Hoth] card into hand from Force Pile");
            action.setActionMsg("Take a [Hoth] card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.icon(Icon.HOTH),  true));

            actions.add(action);
        }
        return actions;
    }
}
