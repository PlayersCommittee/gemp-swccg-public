package com.gempukku.swccgo.cards.set206.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Interrupt
 * Subtype: Used
 * Title: Any Methods Necessary (V)
 */
public class Card206_013 extends AbstractUsedInterrupt {
    public Card206_013() {
        super(Side.DARK, 4, Title.Any_Methods_Necessary, Uniqueness.UNIQUE);
        setLore("Darth Vader authorized the bounty hunters to use any means at their disposal to find and capture the Millennium Falcon - not that they need any encouragement.");
        setGameText("If Jodo Kast or your Fett was just targeted by a weapon, cancel the targeting and place weapon (unless a [Permanent Weapon]) in owner's Used Pile. OR [Upload] (or [download] as a 'react'): Binders, Jet Pack, Mandalorian Armor, or a blaster rifle.");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_6);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.or(Filters.Jodo, Filters.and(Filters.your(self), Filters.Fett)), Filters.any)) {
            final PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                            if (weapon != null && Filters.not(Icon.PERMANENT_WEAPON).accepts(game, weapon)) {
                                action.appendEffect(
                                        new PlaceCardInUsedPileFromTableEffect(action, weapon));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_METHODS_NECESSARY__UPLOAD_OR_DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Binders, Jet Pack, Mandalorian Armor, or a blaster rifle into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Binders, Filters.Jet_Pack, Filters.Mandalorian_Armor, Filters.blaster_rifle), true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.ANY_METHODS_NECESSARY__UPLOAD_OR_DOWNLOAD_CARD;

        // Check condition(s)
        if ((TriggerConditions.battleInitiated(game, effectResult, opponent)
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy card as 'react' from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Binders, Jet Pack, Mandalorian Armor, or a blaster rifle as a 'react' from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Binders, Filters.Jet_Pack, Filters.Mandalorian_Armor, Filters.blaster_rifle), false, true, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}