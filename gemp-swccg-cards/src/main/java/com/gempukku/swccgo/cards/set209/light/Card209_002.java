package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Ruescott Melshi
 */
public class Card209_002 extends AbstractRebel {
    public Card209_002() {
        super(Side.LIGHT, 3, 3, 3, 2, 4, "Commander Ruescott Melshi", Uniqueness.UNIQUE);
        setLore("Scout trooper.");
        setGameText("When deployed (or if just lost), may [upload] Throw Me Another Charge or a trooper. Opponent's troopers are power and forfeit -1 here. During any move phase, may use 2 Force to release an imprisoned captive here.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.SCOUT, Keyword.TROOPER, Keyword.COMMANDER, Keyword.CAN_RELEASE_CAPTIVES);
        addPersona(Persona.MELSHI);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_RUESCOTT_MELSHI__UPLOAD_TMAC_OR_TROOPER_WHEN_DEPLOYED_OR_LOST;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take Throw Me Another Charge or a trooper into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Throw_Me_Another_Charge, Filters.trooper), true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_RUESCOTT_MELSHI__UPLOAD_TMAC_OR_TROOPER_WHEN_DEPLOYED_OR_LOST;

        // Check condition(s)
        if(TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take Throw Me Another Charge or a trooper into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Throw_Me_Another_Charge, Filters.trooper), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentsTroopersHere = Filters.and(Filters.opponents(self), Filters.trooper, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, opponentsTroopersHere, -1));
        modifiers.add(new ForfeitModifier(self, opponentsTroopersHere, -1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        Filter targetFilter = Filters.and(Filters.your(playerId), Filters.captive, Filters.imprisoned, Filters.at(Filters.here(self)));
        Map<InactiveReason, Boolean> spotOverride = SpotOverride.INCLUDE_CAPTIVE;

        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTarget(game, self, spotOverride, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target captive", spotOverride, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Release " + GameUtils.getCardLink(cardTargeted),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReleaseCaptiveEffect(action, cardTargeted));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

}
