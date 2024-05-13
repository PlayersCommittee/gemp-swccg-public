package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Device
 * Title: Shield Gate
 */
public class Card216_018 extends AbstractDevice {
    public Card216_018() {
        super(Side.DARK, 0, PlayCardZoneOption.ATTACHED, Title.Shield_Gate, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("Deploy on Scarif system. If a starship was just lost from here or opponent just Force drained here, opponent may draw destiny. Add 1 for each Scarif location opponent occupies. If total destiny > 8, Shield Gate 'blown away' (place out of play).");
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.isBlownAway(game, Filters.Shield_Gate);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Scarif_system;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.starship, Filters.hasAttached(self))
                || TriggerConditions.forceDrainCompleted(game, effectResult, playerId, Filters.hasAttached(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Attempt to blow away " + GameUtils.getCardLink(self));
            action.setActionMsg("Attempt to blow away " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null) {
                                game.getGameState().sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }

                            float attemptTotal = game.getModifiersQuerying().getBlowAwayShieldGateAttemptTotal(game.getGameState(), totalDestiny);

                            int scarifLocationsOccupiedByOpponent = Filters.countActive(game, self, Filters.and(Filters.Scarif_location, Filters.occupies(game.getOpponent(self.getOwner()))));
                            attemptTotal = attemptTotal + scarifLocationsOccupiedByOpponent;
                            game.getGameState().sendMessage("Total: " + attemptTotal);
                            if (attemptTotal > 8) {
                                game.getGameState().sendMessage("Result: Success.");
                                action.appendEffect(
                                        new BlowAwayEffect(action, self) {
                                            @Override
                                            protected StandardEffect getAdditionalGameTextEffect(SwccgGame game, Action blowAwaySubAction) {
                                                return new PlaceCardOutOfPlayFromTableEffect(blowAwaySubAction, self);
                                            }
                                        });
                            } else {
                                game.getGameState().sendMessage("Result: Failed.");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
