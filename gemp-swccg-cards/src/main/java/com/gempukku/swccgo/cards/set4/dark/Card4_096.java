package com.gempukku.swccgo.cards.set4.dark;

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
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Gherant
 */
public class Card4_096 extends AbstractImperial {
    public Card4_096() {
        super(Side.DARK, 2, 3, 3, 2, 4, "Commander Gherant", Uniqueness.UNIQUE);
        setLore("Deck officer aboard the Super Star Destroyer Executor. Responsible for preventing unauthorized access to sensitive areas of the Empire's flagship. Hand-picked by Captain Piett.");
        setGameText("If aboard Executor (even at an Executor site), once during each of your deploy phases, may use 2 Force to search your Reserve Deck and take any one Executor site into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER);
        setMatchingStarshipFilter(Filters.Executor);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_GHERANT__UPLOAD_EXECUTOR_SITE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isAboard(game, self, Persona.EXECUTOR)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take an Executor site into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Executor_site, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
