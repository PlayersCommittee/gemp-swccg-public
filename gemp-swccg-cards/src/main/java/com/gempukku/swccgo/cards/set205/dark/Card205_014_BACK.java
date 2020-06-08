package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.TakeUnattendedFrozenCaptiveIntoCustodyEffect;
import com.gempukku.swccgo.logic.modifiers.CrossOverAttemptTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, The Emperor's Prize
 */
public class Card205_014_BACK extends AbstractRebel {
    public Card205_014_BACK() {
        super(Side.DARK, 7, 0, 6, 6, 0, "Luke Skywalker, The Emperor's Prize", Uniqueness.UNIQUE);
        setGameText("Luke Skywalker, The Emperor's Prize is a Dark Side card and does not count toward your deck limit. Reveal to opponent when deploying your Starting Effect. Deploys to Death Star II: Throne Room only at start of game as a frozen captive if Bring Him Before Me on table and Your Destiny is suspended; otherwise place out of play. For remainder of game, may not be placed in Reserve Deck. Luke Skywalker, The Emperor's Prize is a persona of Luke only while on table. While this side up, Bring Him Before Me may not flip. May not be escorted. Flip this card if Vader present and not escorting a captive. Place out of play if released or about to leave table.");
        setDoesNotCountTowardDeckLimit(true);
        addPersona(Persona.LUKE);
        setCharacterPersonaOnlyWhileOnTable(true);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CrossOverAttemptTotalModifier(self, -3));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                            }
                        }
                    });
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)
                && TriggerConditions.isTableChanged(game, effectResult)) {
            final PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.not(Filters.escorting(self))));
            if (vader != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("'Thaw'");
                action.setActionMsg("'Thaw' " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
                action.appendEffect(
                        new TakeUnattendedFrozenCaptiveIntoCustodyEffect(action, vader, self, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
