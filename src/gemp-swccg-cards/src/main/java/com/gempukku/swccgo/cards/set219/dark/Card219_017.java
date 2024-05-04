package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawOneCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Device
 * Title: Observation Holocam (V)
 */
public class Card219_017 extends AbstractDevice {
    public Card219_017() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Observation Holocam", Uniqueness.UNRESTRICTED, ExpansionSet.SET_19, Rarity.V);
        setVirtualSuffix(true);
        setLore("Remote surveillance viewers with droid controllers supplement security. Can activate alarms and automated weapons when needed, bringing help to endangered locations.");
        setGameText("Deploy on your interior site. If opponent just deployed a character or vehicle here, " +
                    "may lose 1 Force to move your character to here using landspeed (at normal use of the Force). " +
                    "Opponent may not 'react' to here. May place out of play to draw top card of Reserve Deck.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.interior_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.here(self), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");

            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new DrawOneCardFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.hasNotPerformedRegularMove,
                Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, false, 0, null, Filters.here(self)));


        if(TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.or(Filters.character, Filters.vehicle), Filters.here(self))) {

            Collection<PhysicalCard> canMove = Filters.filterActive(game, self, filter);
            if (!canMove.isEmpty()) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Move a character to here");
                action.setActionMsg("Move a character to here using landspeed");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a character to move to here using landspeed ", Filters.in(canMove)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.appendCost(
                                new LoseForceEffect(action, playerId, 1));
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(
                                        new MoveCardUsingLandspeedEffect(action, playerId, finalTarget, false, Filters.here(self)));
                            }
                        });
                    }
                });
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}