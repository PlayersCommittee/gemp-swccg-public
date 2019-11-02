package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Effect
 * Title: Overseeing It Personally (V) (Errata)
 */
public class Card501_014 extends AbstractNormalEffect {
    public Card501_014() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Overseeing It Personally", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Though reluctant to leave Coruscant. Emperor Palpatine occasionally finds it necessary to personally put lagging Imperial operations back on schedule.");
        setGameText("Deploy on a your leader. Opponent may not cancel or reduce Force drains at same battleground. If on an Imperial, once per game, may place this Effect out of play to retrieve Vader.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_5);
        setTestingText("Overseeing It Personally (V) (Errata)");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self.getOwner()), Filters.leader);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.leader;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter sameBattleground = Filters.and(Filters.sameLocation(self), Filters.battleground);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, sameBattleground, opponent, null));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, sameBattleground, opponent, null));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVERSEEING_IT_PERSONALLY__PLACE_OUT_OF_PLAY_TO_RETRIEVE_VADER;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isAttachedTo(game, self, Filters.Imperial)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place out of play to retrieve Vader");
            action.setActionMsg("Retrieve Vader");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.Vader));
            return Collections.singletonList(action);
        }
        return null;
    }
}