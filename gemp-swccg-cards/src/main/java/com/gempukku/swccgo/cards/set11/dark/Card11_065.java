package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Watto
 */
public class Card11_065 extends AbstractAlien {
    public Card11_065() {
        super(Side.DARK, 3, 2, 3, 2, 4, Title.Watto, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.R);
        setLore("Toydarian junk dealer. Skilled gambler. Won Shmi and Anakin in a Podrace bet with Gardulla the Hutt. Jedi mind tricks don't work on him, only money.");
        setGameText("May 'fly' (landspeed = 2). Once during each of your turns, may take Watto's Chance Cube into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.TOYDARIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.WATTO__UPLOAD_WATTOS_CHANCE_CUBE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Watto's Chance Cube into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Wattos_Chance_Cube, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
