package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Objective
 * Title: The First Order Reigns / The Resistance Is Doomed
 */
public class Card225_032_BACK extends AbstractObjective {
    public Card225_032_BACK() {
        super(Side.DARK, 7, Title.The_Resistance_Is_Doomed, ExpansionSet.SET_25, Rarity.V);
        setGameText("While this side up, once per turn, may deploy a non-unique trooper (or non-unique [First Order] vehicle) from Lost Pile. While you occupy a Crait location, your Force drains at battlegrounds where you have two First Order characters are +1. While Kylo occupies Salt Plateau, opponent may not Force drain where their character or permanent pilot is alone. While you control Salt Plateau, opponent's Force retrieval is canceled. Place out of play if Kylo just forfeited from a battle you lost at Salt Plateau where Han, Leia, or Luke present.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_FIRST_ORDER_REIGNS__DOWNLOAD_EPISODE_7_BATTLEGROUND;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy an [Episode VII] battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_VII, Filters.battleground), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_RESISTANCE_IS_DOOMED__DOWNLOAD_FIRST_ORDER_VEHICLE_OR_TROOPER;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);

            Filter nonUniqueTrooper = Filters.and(Filters.non_unique, Filters.trooper);
            Filter nonUniqueFOVehicle = Filters.and(Filters.non_unique, Icon.FIRST_ORDER, Filters.vehicle);

            action.setText("Deploy card from Lost Pile");
            action.setActionMsg("Deploy a non-unique trooper (or non-unique [First Order] vehicle) from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.or(nonUniqueTrooper, nonUniqueFOVehicle), false));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter hanLeiaLuke = Filters.or(Filters.Han, Filters.Leia, Filters.Luke);
        Filter locationFilter = Filters.and(Filters.Crait_Salt_Plateau, Filters.wherePresent(self, hanLeiaLuke));
        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.Kylo, locationFilter)
                && GameConditions.isDuringBattleLostBy(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Place objective out of play.");
            action.setActionMsg("Place The First Order Reigns / The Resistance Is Doomed out of play.");

            action.appendEffect(new PlaceCardOutOfPlayFromTableEffect(action, self));
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, game.getOpponent(self.getOwner()))
                && GameConditions.controls(game, playerId, Filters.Crait_Salt_Plateau)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel retrieval");
            action.setActionMsg("Force retrieval is canceled");
            action.appendEffect(
                    new CancelForceRetrievalEffect(action)
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        
        // While you occupy a Crait location, your Force drains at battlegrounds where you have two First Order characters are +1.
        Condition youOccupyACraitLocation = new OccupiesCondition(playerId, Filters.Crait_location);
        Filter battlegroundsWithTwoFirstOrderCharacters = Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.and(Filters.First_Order_character, Filters.with(self, Filters.First_Order_character))));
        modifiers.add(new ForceDrainModifier(self, battlegroundsWithTwoFirstOrderCharacters, youOccupyACraitLocation, 1, playerId));

        // While Kylo occupies Salt Plateau, opponent may not Force drain where their character or permanent pilot is alone.
        Condition kyloOccupiesSaltPlateau = new OccupiesWithCondition(self, playerId, Filters.Crait_Salt_Plateau, Filters.Kylo);
        Filter locationHasOneCardsWithAbility = Filters.and(Filters.sameLocationAs(self, Filters.and(Filters.opponents(self), Filters.characterOrPermanentPilotAlone)));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.sameLocationAs(self, locationHasOneCardsWithAbility), kyloOccupiesSaltPlateau, opponent));

        return modifiers;
    }

}