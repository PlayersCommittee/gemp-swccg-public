package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPersonalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Wokling (V)
 */
public class Card200_047 extends AbstractNormalEffect {
    public Card200_047() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Wokling, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Every Ewok is taught to be protective of the younger members of their clan.");
        setGameText("Unless Massassi Throne Room on table, deploy on table. Your personal Force generation = 2. Once per game, may use 3 Force to [upload] an Effect that has no deploy cost and deploys on another card. May place this Effect out of play to retrieve 1 Force. [Immune to Alter.]");
        addIcons(Icon.ENDOR, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !Filters.canSpot(game, self, Filters.Massassi_Throne_Room);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetPersonalForceGenerationModifier(self, 2, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.WOKLING__UPLOAD_EFFECT;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take an Effect into hand from Reserve Deck");
            action.setActionMsg("Take an Effect that has no deploy cost and deploys on another card into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.effectThatDeploysOnAnotherCard), true));
            actions.add(action);
        }

        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Place out of play to retrieve 1 Force");
        action.setActionMsg("Retrieve 1 Force");
        // Pay cost(s)
        action.appendCost(
                new PlaceCardOutOfPlayFromTableEffect(action, self));
        // Perform result(s)
        action.appendEffect(
                new RetrieveForceEffect(action, playerId, 1));
        actions.add(action);

        return actions;
    }
}