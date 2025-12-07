package com.gempukku.swccgo.cards.set226.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfForcePileEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Jedi Master
 * Title: Kelnacca
 */
public class Card226_019 extends AbstractJediMaster {
    public Card226_019() {
        super(Side.LIGHT, 2, 5, 5, 7, 7, "Kelnacca", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Wookiee.");
        setGameText("Once per turn, if Wookiee Homestead on table, may place a card from hand on top of Force Pile to draw bottom card from Force Pile. Once per game, may [download] a unique (•) Forest. Immune to Wookiee Strangle and You Are Beaten.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_26);
        setSpecies(Species.WOOKIEE);
        addImmuneToCardTitle(Title.Wookiee_Strangle);
        addImmuneToCardTitle(Title.You_Are_Beaten);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId1 = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId1)
                && GameConditions.canSpot(game, self, Filters.Wookiee_Homestead)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId1);
            action1.setText("Place card from hand on top of Force Pile");
            action1.setActionMsg("Draw bottom card of Force Pile.");
            // Update usage limit(s)
            action1.appendUsage(
                new OncePerTurnEffect(action1));
            // Pay cost(s)
            action1.appendCost(
                new PutCardFromHandOnForcePileEffect(action1, playerId));
            // Perform result(s)
            action1.appendEffect(
                new DrawCardIntoHandFromBottomOfForcePileEffect(action1, playerId));
            actions.add(action1);
        }

        GameTextActionId gameTextActionId2 = GameTextActionId.KELNACCA__DOWNLOAD_FOREST;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId2)) {

            final TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action2.setText("Deploy forest from Reserve Deck");
            action2.setActionMsg("Deploy a unique Forest from Reserve Deck");
            // Update usage limit(s)
            action2.appendUsage(
                    new OncePerGameEffect(action2));
            // Perform result(s)
            action2.appendEffect(
                    new DeployCardFromReserveDeckEffect(action2, Filters.and(Filters.unique, Filters.forest), true));
            actions.add(action2);
        }
        return actions;
    }
}
