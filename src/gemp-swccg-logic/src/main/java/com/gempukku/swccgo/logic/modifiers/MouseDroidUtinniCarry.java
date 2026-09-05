package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Mouse Droid "carry" vs "hunted target" helper.
 *
 * Forum nuance: the mouse carries a relocated Utinni Effect; it does not become the hunted target.
 * Original targets keep the Utinni's game-text modifiers while the Utinni card is attached to the mouse.
 *
 * Thin / reversible: delete this class and the call sites (Filters.hasAttached, Mouse relocate,
 * ModifiersLogic Card Info) if Rules Committee reverses the carry ruling.
 */
public final class MouseDroidUtinniCarry {
    private MouseDroidUtinniCarry() {
    }

    public static boolean isMouseDroid(PhysicalCard card) {
        if (card == null) {
            return false;
        }
        if (Title.Mouse_Droid.equals(card.getTitle())) {
            return true;
        }
        return card.getBlueprint() != null && Title.Mouse_Droid.equals(card.getBlueprint().getTitle());
    }

    public static boolean isUtinniEffect(PhysicalCard card) {
        return card != null
                && card.getBlueprint() != null
                && card.getBlueprint().isCardType(CardType.EFFECT)
                && card.getBlueprint().getCardSubtype() == CardSubtype.UTINNI;
    }

    /**
     * True when this Utinni is physically attached to a Mouse Droid (carrier, not hunted target).
     */
    public static boolean isCarriedByMouseDroid(PhysicalCard utinni) {
        if (!isUtinniEffect(utinni)) {
            return false;
        }
        PhysicalCard host = utinni.getAttachedTo();
        return isMouseDroid(host);
    }

    /**
     * When Mouse relocates an Utinni off a character/starship/vehicle, remember that host as the hunted
     * target if no Utinni hunt TargetId is set yet (deploy-on-character Utinnis like Juri Juice).
     * Does not overwrite existing hunt targets (SADD, Destroyed Homestead, etc.).
     * Ignores DEPLOY_TARGET, which always tracks the physical host and becomes the mouse after relocate.
     */
    public static void preservePreviousHostAsHuntedTargetIfNeeded(PhysicalCard utinni, PhysicalCard previousHost, GameState gameState) {
        if (utinni == null || previousHost == null || gameState == null) {
            return;
        }
        if (!isUtinniEffect(utinni)) {
            return;
        }
        CardCategory category = previousHost.getBlueprint().getCardCategory();
        if (category != CardCategory.CHARACTER && category != CardCategory.STARSHIP && category != CardCategory.VEHICLE) {
            return;
        }
        if (isMouseDroid(previousHost)) {
            return;
        }
        if (utinni.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1) != null
                || utinni.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2) != null) {
            return;
        }
        utinni.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, previousHost, Filters.any);
    }

    /**
     * For Filters.hasAttached(utinni): while carried by Mouse, treat Utinni hunt TargetId card(s) as the
     * effect host instead of the mouse. Otherwise use real attachedTo.
     */
    public static boolean acceptsAsEffectHost(GameState gameState, ModifiersQuerying modifiersQuerying,
                                              PhysicalCard utinni, PhysicalCard candidate) {
        if (utinni == null || candidate == null) {
            return false;
        }
        if (isCarriedByMouseDroid(utinni)) {
            for (TargetId targetId : new TargetId[] { TargetId.UTINNI_EFFECT_TARGET_1, TargetId.UTINNI_EFFECT_TARGET_2 }) {
                PhysicalCard hunted = utinni.getTargetedCard(gameState, targetId);
                if (hunted != null && Filters.sameCardId(hunted).accepts(gameState, modifiersQuerying, candidate)) {
                    return true;
                }
            }
            return false;
        }
        PhysicalCard attachedTo = utinni.getAttachedTo();
        return attachedTo != null
                && Filters.sameCardId(attachedTo).accepts(gameState, modifiersQuerying, candidate);
    }

    /**
     * Card Info: while Mouse carries an Utinni that still has hunted Utinni TargetId data, do not treat the
     * mouse as targeted merely because the Utinni is attached (mouse is host/carrier only).
     */
    public static boolean shouldSkipAttachmentTargetingForCardInfo(GameState gameState, PhysicalCard attachedCard, PhysicalCard host) {
        if (!isUtinniEffect(attachedCard) || !isMouseDroid(host) || gameState == null) {
            return false;
        }
        return attachedCard.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1) != null
                || attachedCard.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2) != null;
    }
}