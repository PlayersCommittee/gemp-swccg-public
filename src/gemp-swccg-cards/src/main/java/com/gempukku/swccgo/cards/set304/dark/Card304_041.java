package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Capital
 * Title: ISN Vigiliant
 */
public class Card304_041 extends AbstractCapitalStarship {
    public Card304_041() {
        super(Side.DARK, 1, 4, 2, 3, null, 5, 4, "ISN Vigiliant", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Commissioned as an electronic warfare platform, the ISN  Vigiliant is loaded with the most advanced senors and jammers available. ");
        setGameText("May add 2 pilots, 4 passengers, and 2 TIEs. Permanent pilot provides ability of 1. Once per game, if opponent's starship just moved from here, Vigiliant may follow it as a regular move (if within range).");
        addIcons(Icon.CSP, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
		addModelType(ModelType.INQUISITOR_CLASS_CORVETTE);
        setPilotCapacity(2);
        setPassengerCapacity(4);
        setTIECapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VIGILIANT_FOLLOW_STARSHIP;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && TriggerConditions.movedFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.starship), Filters.here(self))) {
            MovedResult movedResult = (MovedResult) effectResult;
            Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            if (Filters.movableAsRegularMove(playerId, false, 0, false, toLocation).accepts(game, self)) {
                PhysicalCard starshipMoved = movedResult.getMovedCards().iterator().next();

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Follow " + GameUtils.getFullName(starshipMoved));
                action.setActionMsg("Follow " + GameUtils.getCardLink(starshipMoved) + " as a regular move");
                action.appendUsage(new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new MoveCardAsRegularMoveEffect(action, playerId, self, false, false, toLocation));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
