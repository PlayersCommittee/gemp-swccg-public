package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Hunchback
 */
public class Card2_010 extends AbstractAlien {
    public Card2_010() {
        super(Side.LIGHT, 3, 4, 4, 1.5, 4, "Hunchback", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("What hump?");
        setGameText("On a hunch, if opponent just initiated a Force drain at an adjacent site, you may use 1 Force to search opponent's Force Pile and place out of play one card there that has 'back' in the title; reshuffle.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.HUNCHBACK__SEARCH_FORCE_PILE;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.adjacentSite(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canSearchOpponentsForcePile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search opponent's Force Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromForcePileEffect(action, playerId, opponent, Filters.titleContains("back"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
