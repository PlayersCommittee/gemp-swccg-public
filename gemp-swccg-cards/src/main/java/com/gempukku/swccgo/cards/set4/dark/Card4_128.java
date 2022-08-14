package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Much Anger In Him
 */
public class Card4_128 extends AbstractNormalEffect {
    public Card4_128() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Much Anger In Him", Uniqueness.UNIQUE);
        setLore("'Adventure, heh. Excitement, heh. A Jedi craves not these things. You are reckless.'");
        setGameText("Deploy on a Rebel. At the end of each opponent's battle phases, if you have presence at the location where that Rebel has presence and a battle did not take place there, opponent loses 4 Force.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Rebel;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Rebel;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.BATTLE)) {
            PhysicalCard location = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.occupies(playerId),
                    Filters.occupiesWith(opponent, self, Filters.hasAttached(self)), Filters.battleNotOccurredAtLocation));
            if (location != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose 4 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 4));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}