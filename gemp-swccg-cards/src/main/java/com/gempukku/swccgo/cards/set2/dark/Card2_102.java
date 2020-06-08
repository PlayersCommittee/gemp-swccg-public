package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Reegesk
 */
public class Card2_102 extends AbstractAlien {
    public Card2_102() {
        super(Side.DARK, 3, 3, 1, 1, 3, Title.Reegesk, Uniqueness.UNIQUE);
        setLore("Ranat thief and scavenger from Aralia. Regularly trades with Jawas. Adept at pilfering items without alerting the owner. Willing to steal anything, even trash.");
        setGameText("Once during each of your control phases, may lose 1 Force to take one vehicle, droid, weapon or device from opponent's Lost Pile or Crash Site Memorial into hand to use as your own.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.THIEF, Keyword.SCAVENGER);
        setSpecies(Species.RANAT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.REEGESK__TAKE_CARD_FROM_OPPONENTS_LOST_PILE_OR_CRASH_SITE_MEMORIAL;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            String opponent = game.getOpponent(playerId);
            Filter filter = Filters.or(Filters.vehicle, Filters.droid, Filters.weapon, Filters.device);
            Filter crashSiteMemorialFilter = Filters.and(Filters.opponents(self), Filters.Crash_Site_Memorial, Filters.hasStacked(filter));

            List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
            if (GameConditions.canTakeCardsIntoHandFromOpponentsLostPile(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card from Lost Pile");
                action.setActionMsg("Take a vehicle, droid, weapon, or device into hand from opponent's Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromLostPileEffect(action, playerId, opponent, filter, false));
                actions.add(action);
            }

            // Check condition(s)
            if (GameConditions.canSpot(game, self, crashSiteMemorialFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card from Crash Site Memorial");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                // Perform result(s)
                action.appendEffect(
                        new TakeStackedCardIntoHandEffect(action, playerId, crashSiteMemorialFilter, filter));
                actions.add(action);
            }
            return actions;
        }
        return null;
    }
}
