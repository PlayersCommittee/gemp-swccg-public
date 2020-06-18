package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Planetary Rings
 */
public class Card501_011 extends AbstractMobileEffect {
    public Card501_011() {
        super(Side.DARK, 4, "Planetary Rings", Uniqueness.DIAMOND_1);
        setLore("'We're gonna get pulverized if we stay out here much longer.'");
        setGameText("Deploy on a planet system (except Dagobah). Sectors drawn for asteroid destiny are cancelled and redrawn. Once during your turn, you may deploy a sector here from reserve deck; reshuffle or make an additional regular move from a related sector. Immune to alter.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.planet_system, Filters.not(Filters.title(Title.Dagobah)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isAsteroidDestinyJustDrawnMatching(game, effectResult, Filters.sector)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            action.setText("Cancel and redraw asteroid destiny");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a sector here");
            action.setActionMsg("Deploy a sector here");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.sector, self.getAttachedTo().getTitle(), true));
            actions.add(action);

        }

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make an additional regular move");
            action.setActionMsg("Make an additional regular move");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose starship to make an additional move", Filters.relatedSector(self.getAttachedTo())) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            action.appendEffect(
                                    new MoveCardAsRegularMoveEffect(action, playerId, selectedCard, false, true, Filters.sameOrRelatedLocation(self.getAttachedTo())));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}