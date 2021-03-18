package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 15
 * Type: Device
 * Title: Shield Gate
 */
public class Card501_077 extends AbstractDevice {
    public Card501_077() {
        super(Side.DARK, 0, PlayCardZoneOption.ATTACHED, Title.Shield_Gate, Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Scarif. May deploy Citadel Tower from Reserve Deck; reshuffle. If a starship was just lost from here or opponent just Force drained here, opponent may draw destiny. Add 1 for each spy out of play. If total destiny > 8, Shield Gate \"blown away\" (place out of play).");
        addIcons(Icon.VIRTUAL_SET_15);
        setVirtualSuffix(true);
        setTestingText("Shield Gate");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Scarif_system;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SCARIF__DOWNLOAD_CITADEL_TOWER;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Scarif_Citadel_Tower)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Citadel Tower from Reserve Deck");
            action.setActionMsg("Deploy Citadel Tower from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Scarif_Citadel_Tower), true));
            return Collections.singletonList(action);
        }
        return null;
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
                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.ADD_TWO_TO_BLOW_AWAY_SHIELD_GATE)) {
                                totalDestiny = totalDestiny + 2;
                            }
                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SUBTRACT_TWO_FROM_BLOW_AWAY_SHIELD_GATE)) {
                                totalDestiny = totalDestiny - 2;
                            }
                            Collection<PhysicalCard> outOfPlaySpies = Filters.filter(game.getGameState().getAllOutOfPlayCards(), game, Filters.spy);
                            int numSpiesOutOfPlay = outOfPlaySpies.size();
                            totalDestiny = totalDestiny + numSpiesOutOfPlay;
                            game.getGameState().sendMessage("Total: " + totalDestiny);
                            if (totalDestiny > 8) {
                                game.getGameState().sendMessage("Result: Success. " + GameUtils.getCardLink(self) + " is 'blown away'");
                                action.appendEffect(
                                        new PlaceCardOutOfPlayFromTableEffect(action, self)
                                );
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
