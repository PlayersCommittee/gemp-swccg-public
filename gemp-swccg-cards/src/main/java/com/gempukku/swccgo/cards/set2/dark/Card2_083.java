package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Khurgee
 */
public class Card2_083 extends AbstractImperial {
    public Card2_083() {
        super(Side.DARK, 2, 3, 2, 2, 3, "Captain Khurgee", Uniqueness.UNIQUE);
        setLore("Docking bay security officer. Leader. Honored for bravery aboard the Star Destroyer Thunderflare where he rescued five officers from the wreckage of a shuttle crash.");
        setGameText("Once during each of your control phases, you may use 2 Force to take one Scanning Crew into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_KHURGEE__UPLOAD_SCANNING_CREW;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Scanning Crew into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Scanning_Crew, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
