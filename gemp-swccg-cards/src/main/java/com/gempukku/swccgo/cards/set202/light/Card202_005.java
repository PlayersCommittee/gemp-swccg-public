package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 2
 * Type: Effect
 * Title: Like My Father Before Me
 */
public class Card202_005 extends AbstractNormalEffect {
    public Card202_005() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Like_My_Father_Before_Me, Uniqueness.UNIQUE);
        setLore("The final act of Luke's ascension to Jedi Knighthood was sending off his father, draped in the garb of Vader.");
        setGameText("Deploy on table if an Endor site on table. [Death Star II] Luke is deploy = 6 and may not be Disarmed. If Luke at a battleground (even if captive), your battle destiny draws are + 1 there and, during your turn, may [download] Luke's Lightsaber or place a card from hand on Force Pile. Immune to Alter.");
        addIcons(Icon.VIRTUAL_SET_2);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpotFromTopLocationsOnTable(game, Filters.Endor_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter deathStarIILuke = Filters.and(Icon.DEATH_STAR_II, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetDeployCostModifier(self, deathStarIILuke, 6));
        modifiers.add(new MayNotBeDisarmedModifier(self, deathStarIILuke));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.sameLocationAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Luke), new AtCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Luke, Filters.battleground), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LIKE_MY_FATHER_BEFORE_ME__DOWNLOAD_LUKES_LIGHTSABER_OR_PLACE_CARD_ON_FORCE_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LUKES_LIGHTSABER)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Luke, Filters.at(Filters.battleground)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Luke's Lightsaber from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Lukes_Lightsaber, true));
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Luke, Filters.at(Filters.battleground)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand on Force Pile");
            action.setActionMsg("Place a card from hand on Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromHandOnForcePileEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}