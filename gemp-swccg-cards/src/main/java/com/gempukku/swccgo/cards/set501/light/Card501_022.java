package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Rio Durant
 */
public class Card501_022 extends AbstractAlien {
    public Card501_022() {
        super(Side.LIGHT, 4, 3, 3, 2, 4, "Rio Durant", Uniqueness.UNIQUE);
        setLore("Ardennian smuggler and thief.");
        setGameText("Adds 2 to power of anything he pilots. Once per turn, if you just deployed a smuggler here, may place a card from hand in Used Pile to activate 2 Force. Once per game, if you just won a battle here, may steal a starfighter from opponent's Lost Pile into hand.");
        addPersona(Persona.RIO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.THIEF);
        setSpecies(Species.ARDENNIAN);
        setMatchingStarshipFilter(Filters.or(Filters.stolen, Filters.icon(Icon.INDEPENDENT)));
        setTestingText("Rio Durant");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, Filters.and(Filters.your(self.getOwner()),Filters.smuggler), Filters.here(self))
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 2 force");
            action.setActionMsg("Place a card from hand on used pile to activate 2 force");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendCost(
                    new PutCardFromHandOnUsedPileEffect(action, playerId)
            );
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 2)
            );

            actions.add(action);
        }

        GameTextActionId gameTextActionId1 = GameTextActionId.RIO_DURANT__STEAL_STARSHIP;
        if (TriggerConditions.wonBattle(game, effectResult, playerId)
                && GameConditions.hasLostPile(game, game.getOpponent(playerId))
                && GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId1)
                && GameConditions.isOncePerGame(game, self, gameTextActionId1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Steal a starfighter into hand");
            action.setActionMsg("Steal a starfighter from opponent's Lost Pile");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.starfighter));
            actions.add(action);
        }
        return actions;
    }
}

