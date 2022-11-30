package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationUsingLandspeedModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Rayno Vaca
 */
public class Card14_085 extends AbstractRepublic {
    public Card14_085() {
        super(Side.DARK, 3, 0, 1, 1, 1, "Rayno Vaca", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Coruscant taxi driver who barely makes enough money to get by. Will try any trick in the book to get an extra credit from his customer.");
        setGameText("Your characters may move from here using their landspeed for free. During your control phase, may place Vaca in Used Pile to take Lana Dobreed into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeFromLocationUsingLandspeedModifier(self, Filters.and(Filters.your(self), Filters.character), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RAYNO_VACA__UPLOAD_LANA_DOBREED;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Lana Dobreed into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Lana_Dobreed, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
