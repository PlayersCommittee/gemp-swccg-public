package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: PT Set 12 (Errata)
 * Type: Effect
 * Title: Shadows Of The Empire
 */


public class Card501_006 extends AbstractNormalEffect {
    public Card501_006() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Shadows Of The Empire", Uniqueness.UNIQUE);
        setLore("");
        setGameText ("If Agents Of Black Sun on table, deploy on table. Once per turn, may use 1 Force to [download] Imperial Square. Once per turn, if Emperor on Coruscant, may draw top card of Force Pile (if during your turn and you occupy three battlegrounds, opponent also loses 1 Force). [Immune to Alter.]");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_12);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Shadows of the Empire errata (-1 force to deploy Imp. Square)");
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Agents_Of_Black_Sun);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId;

        // Check conditions for Coruscant site download action
        gameTextActionId = GameTextActionId.SHADOWS_OF_THE_EMPIRE__DOWNLOAD_IMPERIAL_SQUARE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && !GameConditions.canSpot(game, self, Filters.Coruscant_Imperial_Square)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Coruscant: Imperial Square from Reserve Deck");
            action.setActionMsg("Deploy Coruscant: Imperial Square from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_Imperial_Square, true));
            actions.add(action);
        }


        // Check conditions for drawing top card of force pile
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, Filters.on(Title.Coruscant)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Force Pile");
            action.setActionMsg("Draw top card of Force Pile");

            // Update usage limits
            action.appendUsage(new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(new DrawCardIntoHandFromForcePileEffect(action, playerId));

            if (GameConditions.occupies(game, playerId, 3, Filters.battleground) && GameConditions.isDuringYourTurn(game, self)) {
                String opponent = game.getOpponent(playerId);
                action.appendEffect(new LoseForceEffect(action, opponent, 1));
            }

            actions.add(action);
        }

        return actions;
    }
}


public class Card209_043  {

}