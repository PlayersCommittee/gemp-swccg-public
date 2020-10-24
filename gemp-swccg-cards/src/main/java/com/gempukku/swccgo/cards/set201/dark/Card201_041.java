package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 1
 * Type: Starship
 * Subtype: Capital
 * Title: Stalker (V)
 */
public class Card201_041 extends AbstractCapitalStarship {
    public Card201_041() {
        super(Side.DARK, 1, 8, 8, 7, null, 3, 9, Title.Stalker, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Originally assigned to search the Outer Rim for new worlds to subjugate. Launched the probe droid that found Echo Base. Later reassigned to Death Squadron.");
        setGameText("May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Permanent pilot provides ability of 1. If opponent's starship just moved from here, Stalker may follow it as a regular move (if withing range).");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_1);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        addKeywords(Keyword.DEATH_SQUADRON);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.starship), Filters.here(self))) {
            MovedResult movedResult = (MovedResult) effectResult;
            Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            if (Filters.movableAsRegularMove(playerId, false, 0, false, toLocation).accepts(game, self)) {
                PhysicalCard starshipMoved = movedResult.getMovedCards().iterator().next();

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Follow " + GameUtils.getFullName(starshipMoved));
                action.setActionMsg("Follow " + GameUtils.getCardLink(starshipMoved) + " as a regular move");
                // Perform result(s)
                action.appendEffect(
                        new MoveCardAsRegularMoveEffect(action, playerId, self, false, false, toLocation));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
