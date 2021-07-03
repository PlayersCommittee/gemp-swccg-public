package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Imperial
 * Title: Vanee
 */
public class Card209_040 extends AbstractImperial {
    public Card209_040() {
        super(Side.DARK, 3, 4, 2, 4, 4, "Vanee", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploys -3 to a site with 'Vader' in game text. Once during your turn, may activate 1 Force. Once per game, may take a card with 'Vader' in title into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.VANEE);
        addIcons(Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteWithVaderInGametext = Filters.and(Filters.site, Filters.gameTextContains("vader"));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, siteWithVaderInGametext));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId activateForceActionId  = GameTextActionId.VANEE_ACTIVATE_ONE_FORCE;

        // Check condition(s) - Once during your turn, may activate 1 Force
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, activateForceActionId)
            && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, activateForceActionId);
            action.setText("Activate 1 Force");
            action.setActionMsg("Activate 1 Force");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            actions.add(action);
        }


        GameTextActionId uploadVaderTitleActionId = GameTextActionId.VANEE_UPLOAD_CARD_WITH_VADER_IN_TITLE;

        // Check condition(s) - Once per game, may take a card with 'Vader' in title into hand from Reserve Deck; reshuffle.
        if (GameConditions.isOncePerGame(game, self, uploadVaderTitleActionId)
                && GameConditions.canSearchReserveDeck(game, playerId, self, uploadVaderTitleActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, uploadVaderTitleActionId);
            action.setText("Take a card with 'Vader' in title into hand");
            action.setActionMsg("Take a card with 'Vader' in title into hand");

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.titleContains("vader"), true));
            actions.add(action);
        }
        return actions;
    }
}