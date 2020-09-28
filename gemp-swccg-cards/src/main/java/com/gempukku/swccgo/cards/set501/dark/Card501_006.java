package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Set: Set 13
 * Type: Character
 * SubType: Imperial
 * Title: The Client
 */
public class Card501_006 extends AbstractImperial {
    public Card501_006() {
        super(Side.DARK, 5, 1, 1, 3, 2, "The Client", Uniqueness.UNIQUE);
        setLore("");
        setGameText("During your control phase, if present at a site and your bounty hunter is at a battleground, opponent loses 1 Force. If you just lost a bounty hunter anywhere, may take a bounty hunter into hand from Reserve Deck; reshuffle. Lost if Gideon here.");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("The Client");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && (GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.at(Filters.battleground), Filters.bounty_hunter)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && (GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.at(Filters.battleground), Filters.bounty_hunter)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isHere(game, self, Filters.persona(Persona.GIDEON))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make " + GameUtils.getCardLink(self) + " lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");

            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_CLIENT__TAKE_BOUNTY_HUNTER_INTO_HAND;

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, playerId, Filters.bounty_hunter)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take bounty hunter into hand");
            action.setActionMsg("Take a bounty hunter into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.bounty_hunter, true));
            return Collections.singletonList(action);
        }
        return null;
    }

}
