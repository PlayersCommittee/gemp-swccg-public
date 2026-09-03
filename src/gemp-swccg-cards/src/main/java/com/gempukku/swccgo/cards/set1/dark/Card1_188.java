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
import com.gempukku.swccgo.game.state.WhileInPlayData;
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
     * True if the host is a location the mouse is present at, or a character/starship/vehicle at that location.
     */
    private boolean hasReachedHost(SwccgGame game, PhysicalCard self, PhysicalCard host) {
        if (host == null || host.getCardId() == self.getCardId()) {
            return false;
        }
        CardCategory category = host.getBlueprint().getCardCategory();
        if (category == CardCategory.LOCATION) {
            return hasReachedLocation(game, self, host);
        }
        if (category == CardCategory.CHARACTER || category == CardCategory.STARSHIP || category == CardCategory.VEHICLE) {
            PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), host);
            return hasReachedLocation(game, self, location);
        }
        return false;
    }

    /**
     * Utinni Effects this mouse has 'reached' and may pick up: not Kessel Run / Spice Mines, able to move,
     * attached to a location the mouse is present at or to a character/starship/vehicle at that location.
     */
    private Filter getRelocatableUtinniEffectFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(
                Filters.Utinni_Effect,
                Filters.except(Filters.or(Filters.Kessel_Run, Filters.Spice_Mines_Of_Kessel)),
                Filters.not(Filters.attachedTo(self)),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        if (modifiersQuerying.mayNotMove(gameState, physicalCard)) {
                            return false;
                        }
                        return hasReachedHost(game, self, physicalCard.getAttachedTo());
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
     * True if the card is a hunted character, starship, or vehicle — not a location and not this mouse.
     */
    private boolean isHuntedTargetCard(PhysicalCard self, PhysicalCard target) {
        if (target == null || target.getCardId() == self.getCardId()) {
            return false;
        }
        CardCategory category = target.getBlueprint().getCardCategory();
        return category == CardCategory.CHARACTER || category == CardCategory.STARSHIP || category == CardCategory.VEHICLE;
    }

    /**
     * A carried Utinni is delivered only when every Utinni-effect target is a hunted character/starship/vehicle
     * at the mouse's location. A location target or a missing target does not count as delivery.
     */
    private boolean isDeliveredUtinni(SwccgGame game, PhysicalCard self, PhysicalCard utinni) {
        GameState gameState = game.getGameState();
        List<TargetId> targetIds = utinni.getBlueprint().getUtinniEffectTargetIds(utinni.getOwner(), game, utinni);
        if (targetIds == null || targetIds.isEmpty()) {
            return false;
        }
        for (TargetId targetId : targetIds) {
            PhysicalCard target = utinni.getTargetedCard(gameState, targetId);
            if (!isHuntedTargetCard(self, target) || !Filters.at(Filters.sameLocation(self)).accepts(game, target)) {
                return false;
            }
        }
        return true;
    }

    private Collection<PhysicalCard> getDeliveredCarriedUtinnis(SwccgGame game, PhysicalCard self) {
        List<PhysicalCard> delivered = new LinkedList<PhysicalCard>();
        Collection<PhysicalCard> attachedUtinnis = Filters.filter(game.getGameState().getAttachedCards(self), game, Filters.Utinni_Effect);
        for (PhysicalCard utinni : attachedUtinnis) {
            if (isDeliveredUtinni(game, self, utinni)) {
                delivered.add(utinni);
            }
        }
        return delivered;
    }

    /**
     * True when this mouse is carrying at least one delivered Utinni Effect.
     */
    private boolean isCarryingDeliveredUtinniEffect(SwccgGame game, PhysicalCard self) {
        return !getDeliveredCarriedUtinnis(game, self).isEmpty();
    }

    /**
     * Where to put a delivered Utinni: hunted target if that is a legal host, else the current site if that site
     * is a legal deploy-on host, else the hunted target so the Utinni can cancel when reached.
     * Never dump it onto a location it could not deploy on (Send A Detachment Down except docking bay;
     * Destroyed Homestead only Lars' Farm).
     */
    private PhysicalCard choosePlaceToPutDeliveredUtinni(SwccgGame game, PhysicalCard self, PhysicalCard utinni, PhysicalCard location) {
        PhysicalCard hunted = null;
        List<TargetId> targetIds = utinni.getBlueprint().getUtinniEffectTargetIds(utinni.getOwner(), game, utinni);
        if (targetIds != null) {
            for (TargetId targetId : targetIds) {
                PhysicalCard target = utinni.getTargetedCard(game.getGameState(), targetId);
                if (isHuntedTargetCard(self, target)) {
                    hunted = target;
                    break;
                }
            }
        }
        boolean locationLegal = false;
        boolean huntedLegal = false;
        try {
            Filter legalHost = utinni.getBlueprint().getValidRelocateEffectTargetFilter(utinni.getOwner(), game, utinni);
            locationLegal = location != null && legalHost.accepts(game, location);
            huntedLegal = hunted != null && legalHost.accepts(game, hunted);
        }
        catch (RuntimeException ignored) {
            locationLegal = false;
        }
        if (huntedLegal) {
            return hunted;
        }
        if (locationLegal) {
            return location;
        }
        // Current site is not a legal deploy-on host. Put it on the hunted target instead of an illegal docking bay.
        if (hunted != null) {
            return hunted;
        }
        return null;
    }

    private void appendPlaceDeliveredUtinnis(RequiredGameTextTriggerAction action, SwccgGame game, PhysicalCard self, Collection<PhysicalCard> delivered) {
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
        for (PhysicalCard utinni : delivered) {
            PhysicalCard host = choosePlaceToPutDeliveredUtinni(game, self, utinni, location);
            if (host != null) {
                action.appendEffect(
                        new AttachCardFromTableEffect(action, utinni, host));
            }
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, effectResult)) {
            return null;
        }

        Collection<PhysicalCard> attachedUtinnis = Filters.filter(game.getGameState().getAttachedCards(self), game, Filters.Utinni_Effect);
        Collection<PhysicalCard> delivered = getDeliveredCarriedUtinnis(game, self);
        boolean hasDelivered = !delivered.isEmpty();
        boolean hasUndelivered = attachedUtinnis.size() > delivered.size();
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);

        if (hasDelivered && hasUndelivered) {
            // One package arrived; leave the others on the mouse and stay in play.
            self.setWhileInPlayData(null);
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_3);
            action.setSingletonTrigger(true);
            action.setText("Deliver Utinni Effect");
            action.setActionMsg("Deliver Utinni Effect from " + GameUtils.getCardLink(self));
            appendPlaceDeliveredUtinnis(action, game, self, delivered);
            return Collections.singletonList(action);
        }

        // Remember delivery even if the Utinni is then stolen off the mouse
        // (choosing Organa's Ceremonial Necklace steal before Return).
        if (hasDelivered) {
            self.setWhileInPlayData(new WhileInPlayData(location));
        }
        else if (GameConditions.cardHasWhileInPlayDataSet(self)) {
            PhysicalCard remembered = self.getWhileInPlayData().getPhysicalCard();
            // Stale flag must not bounce the mouse at a later site, and must not fire while undelivered packages remain.
            if (location == null || remembered == null || location.getCardId() != remembered.getCardId() || !attachedUtinnis.isEmpty()) {
                self.setWhileInPlayData(null);
            }
        }

        if (!GameConditions.cardHasWhileInPlayDataSet(self)) {
            return null;
        }

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
        action.setSingletonTrigger(true);
        action.setText("Return to hand");
        action.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
        // Place any still-attached delivered Utinnis, then return the mouse.
        // A stolen necklace is already on the Imperial, so it is not re-attached here.
        appendPlaceDeliveredUtinnis(action, game, self, attachedUtinnis);
        action.appendEffect(
                new ReturnCardToHandFromTableEffect(action, self));
        return Collections.singletonList(action);
    }
}
