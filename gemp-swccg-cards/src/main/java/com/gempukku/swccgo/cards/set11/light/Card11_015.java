package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardAndOrCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Yotts Orren
 */
public class Card11_015 extends AbstractAlien {
    public Card11_015() {
        super(Side.LIGHT, 2, 2, 2, 2, 3, "Yotts Orren", Uniqueness.UNIQUE);
        setLore("Kadas'sa'Nikto. Smuggler who stole from shipments going in and out of Jabba's Palace. Part of Jabba's sail barge crew. Not trusted by anyone.");
        setGameText("Adds 2 to power of anything he pilots. Opponent's combat vehicles are each power -2 at same site. Once per game, may take Mechanical Failure and/or Free Ride into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.combat_vehicle, Filters.atSameSite(self)), -2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.YOTTS_ORREN__UPLOAD_MECHANICAL_FAILURE_AND_OR_FREE_RIDE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take Mechanical Failure and/or Free Ride into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardAndOrCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Mechanical_Failure, Filters.Free_Ride, true) {
                        @Override
                        public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            return "Choose Mechanical Failure and/or Free Ride to take into hand";
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
