package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: I'm Sorry
 */
public class Card11_072 extends AbstractNormalEffect {
    public Card11_072() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I'm Sorry", Uniqueness.UNIQUE);
        setLore("'I'm sorry, too.'");
        setGameText("Deploy on table. If This Deal Is Getting Worse All The Time on table, once per game may take up to 2 interior Cloud City battlegrounds into hand from Reserve Deck; reshuffle. You may not play Scanning Crew or Imperial Barrier. (Immune to Alter.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IM_SORRY__UPLOAD_BATTLEGROUNDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.This_Deal_Is_Getting_Worse_All_The_Time)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take up to 2 interior Cloud City battlegrounds into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 1, 2, Filters.and(Filters.interior_site, Filters.Cloud_City_location, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Scanning_Crew, Filters.Imperial_Barrier), playerId));
        return modifiers;
    }
}