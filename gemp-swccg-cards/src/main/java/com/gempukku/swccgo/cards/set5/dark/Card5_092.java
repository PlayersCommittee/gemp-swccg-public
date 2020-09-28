package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Bewil
 */
public class Card5_092 extends AbstractImperial {
    public Card5_092() {
        super(Side.DARK, 3, 3, 2, 2, 4, "Captain Bewil", Uniqueness.UNIQUE);
        setLore("Tactical officer from Dentaal. Leader. Familiar with utilizing computer controls to lure an invading enemy into a tactically weak position.");
        setGameText("During your control phase, may search your Reserve Deck, take one Laser Gate, Heart Of The Chasm or Rite Of Passage into hand and reshuffle. Opponent's characters may not move from same site as Bewil to a mobile site.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_BEWIL__UPLOAD_LASER_GATE_HEART_OF_THE_CHASM_OR_RITE_OF_PASSAGE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Laser Gate, Heart Of The Chasm, or Rite Of Passage into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Laser_Gate, Filters.Heart_Of_The_Chasm, Filters.Rite_Of_Passage), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                Filters.sameSite(self), Filters.mobile_site));
        return modifiers;
    }
}
