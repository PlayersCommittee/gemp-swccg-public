package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerTIESubtypeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Brandei (V)
 */
public class Card210_030 extends AbstractImperial {
    public Card210_030() {
        super(Side.DARK, 2, 3, 1, 3, 3, "Commander Brandei", Uniqueness.UNIQUE);
        setLore("Technical Services Officer of the Fleet Support Branch. Responsible for keeping Executor's 12 TIE squadrons serviced and combat ready. Just received transfer to Judicator.");
        setGameText("Adds 2 to power of anything he pilots (3 if Judicator). Your total battle destiny here is +1 for each TIE subtype here or aboard same starship. During your turn, may /\\ one launch bay or non-unique TIE.");
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.COMMANDER);
        setVirtualSuffix(true);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Adds 2 to power of anything he pilots (3 if Judicator)
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Judicator)));

        Filter tiesHere = Filters.and(Filters.here(self), Filters.TIE);
        Filter shipContainingBrandei = Filters.hasAboard(self);
        Filter tiesAboardShip = Filters.and(Filters.TIE, Filters.aboard(shipContainingBrandei));
        Filter tiesToCheck = Filters.or(tiesHere, tiesAboardShip);

        // Your total battle destiny here is +1 for each TIE subtype here or aboard same starship.
        modifiers.add(new TotalBattleDestinyModifier(self, new PerTIESubtypeEvaluator(1, tiesToCheck), self.getOwner()));

        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_BRANDEI_V__UPLOAD_LAUNCH_BAY;

        // During your turn, may /\ one launch bay or non-unique TIE.

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {


            Filter nonUniqueTie = Filters.and(Filters.TIE, Filters.non_unique);
            Filter cardToUpload = Filters.or(nonUniqueTie, Filters.launch_bay);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take one launch bay or non-unique TIE into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, cardToUpload, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
