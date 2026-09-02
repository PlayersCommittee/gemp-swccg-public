package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MouseDroidTargetModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: MSE-6 'Mouse' Droid
 */
public class Card1_188 extends AbstractDroid {
    public Card1_188() {
        super(Side.DARK, 0, 0, 0, 0, Title.Mouse_Droid, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Nicknamed for rodent-like appearance. Delivers orders and sensitive documents. Retractable manipulator arms. Made by Rebaxan Colmuni. Easily frightened.");
        setGameText("Landspeed = 3. Deploys to same site as a character targeted by a Utinni Effect (except Kessel Run). If this droid 'reaches' Utinni Effect, may relocate it here. Upon delivery, 'mouse' droid returns to your hand.");
        addModelType(ModelType.MESSENGER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter characterTargetedByUtinni = Filters.and(Filters.character,
                Filters.targetedByCardOnTable(Filters.and(Filters.Utinni_Effect, Filters.except(Filters.Kessel_Run))));
        return Filters.sameSiteAs(self, characterTargetedByUtinni);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        modifiers.add(new MouseDroidTargetModifier(self, Filters.and(Filters.Utinni_Effect, Filters.attachedTo(self))));
        return modifiers;
    }

    /**
     * AR "reaching": present at the location, or in a starship/vehicle slot of a starship/vehicle that is itself present there.
     * A vehicle parked in a starship cargo bay is not present at the planet/system, so that does not count.
     */
    private boolean hasReachedLocation(SwccgGame game, PhysicalCard self, PhysicalCard location) {
        if (location == null) {
            return false;
        }
        if (Filters.present(location).accepts(game, self)) {
            return true;
        }
        PhysicalCard attachedTo = self.getAttachedTo();
        if (attachedTo == null) {
            return false;
        }
        CardCategory category = attachedTo.getBlueprint().getCardCategory();
        return (category == CardCategory.STARSHIP || category == CardCategory.VEHICLE)
                && Filters.present(location).accepts(game, attachedTo);
    }

    /**
     * Utinni Effects this mouse has 'reached' and may pick up: on a location, not Kessel Run / Spice Mines, and able to be moved.
     */
    private Filter getRelocatableUtinniEffectFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(
                Filters.Utinni_Effect,
                Filters.except(Filters.or(Filters.Kessel_Run, Filters.Spice_Mines_Of_Kessel)),
                Filters.attachedTo(Filters.location),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        if (modifiersQuerying.mayNotMove(gameState, physicalCard)) {
                            return false;
                        }
                        PhysicalCard host = physicalCard.getAttachedTo();
                        return host != null && host.getBlueprint().getCardCategory() == CardCategory.LOCATION
                                && hasReachedLocation(game, self, host);
                    }
                });
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter relocatableUtinni = getRelocatableUtinniEffectFilter(game, self);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, relocatableUtinni)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Relocate Utinni Effect here");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose Utinni Effect to relocate here", relocatableUtinni) {
                        @Override
                        protected void cardSelected(final PhysicalCard utinniEffect) {
                            action.addAnimationGroup(utinniEffect);
                            action.addAnimationGroup(self);
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(utinniEffect) + " to " + GameUtils.getCardLink(self),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AttachCardFromTableEffect(action, utinniEffect, self));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * True when an Utinni Effect carried by this mouse has been reached by its target (delivery).
     */
    private boolean isCarryingDeliveredUtinniEffect(SwccgGame game, PhysicalCard self) {
        GameState gameState = game.getGameState();
        Collection<PhysicalCard> attachedUtinnis = Filters.filter(gameState.getAttachedCards(self), game, Filters.Utinni_Effect);
        for (PhysicalCard utinni : attachedUtinnis) {
            List<TargetId> targetIds = utinni.getBlueprint().getUtinniEffectTargetIds(utinni.getOwner(), game, utinni);
            if (targetIds == null || targetIds.isEmpty()) {
                continue;
            }
            boolean allTargetsReached = true;
            boolean hasTarget = false;
            for (TargetId targetId : targetIds) {
                PhysicalCard target = utinni.getTargetedCard(gameState, targetId);
                if (target == null) {
                    continue;
                }
                hasTarget = true;
                if (!Filters.at(Filters.sameLocation(self)).accepts(game, target)) {
                    allTargetsReached = false;
                    break;
                }
            }
            if (hasTarget && allTargetsReached) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && isCarryingDeliveredUtinniEffect(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setSingletonTrigger(true);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
            // Leave any still-attached Utinni Effects at this location, then return the mouse.
            PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (location != null) {
                Collection<PhysicalCard> attachedUtinnis = Filters.filter(game.getGameState().getAttachedCards(self), game, Filters.Utinni_Effect);
                for (PhysicalCard utinni : attachedUtinnis) {
                    action.appendEffect(
                            new AttachCardFromTableEffect(action, utinni, location));
                }
            }
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}