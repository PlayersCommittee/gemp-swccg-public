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
 * Central Mouse Droid "carry" vs "hunted target" / "effect subject" resolution.
 *
 * Forum nuance: the mouse carries a relocated Utinni Effect; it does not become the hunted target.
 * Original targets keep the Utinni's game-text modifiers while the Utinni card is attached to the mouse.
 *
 * Two host concepts while carried:
 * - Effect subject (previous character/starship/vehicle host): remembered on TargetId.EFFECT_TARGET_1.
 *   Used for Filters.hasAttached(utinni) when the card was deployed on that host (Juri Juice, We're The Bait captive).
 * - Hunt TargetIds (UTINNI_EFFECT_TARGET_1/2): preserved as-is. Site-hosted packages (Plastoid, Tusken Breath Mask)
 *   that key modifiers with hasAttached+TargetId fall back to these when no effect subject was remembered.
 *
 * Prefer this helper over per-card Mouse exceptions. Reversible if Rules Committee reverses the carry ruling.
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

    private static boolean isCharacterStarshipOrVehicle(PhysicalCard card) {
        if (card == null || card.getBlueprint() == null) {
            return false;
        }
        CardCategory category = card.getBlueprint().getCardCategory();
        return category == CardCategory.CHARACTER || category == CardCategory.STARSHIP || category == CardCategory.VEHICLE;
    }

    /**
     * On Mouse relocate: remember previous character/starship/vehicle host as the effect subject
     * (TargetId.EFFECT_TARGET_1), and if no hunt TargetIds exist yet, also store that host as
     * UTINNI_EFFECT_TARGET_1 (deploy-on-character Utinnis like Juri Juice).
     * Does not overwrite existing hunt targets (SADD, We're The Bait Luke, Destroyed Homestead, etc.).
     */
    public static void rememberHostsOnMouseRelocate(PhysicalCard utinni, PhysicalCard previousHost, GameState gameState) {
        if (utinni == null || previousHost == null || gameState == null) {
            return;
        }
        if (!isUtinniEffect(utinni)) {
            return;
        }
        if (!isCharacterStarshipOrVehicle(previousHost) || isMouseDroid(previousHost)) {
            return;
        }
        // Effect subject: always remember previous deploy/physical host when relocating off a card host.
        utinni.setTargetedCard(TargetId.EFFECT_TARGET_1, 0, previousHost, Filters.any);
        // Hunt TargetId only when missing (character-hosted Utinnis with empty getUtinniEffectTargetIds).
        if (utinni.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1) == null
                && utinni.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2) == null) {
            utinni.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, previousHost, Filters.any);
        }
    }

    /**
     * @deprecated use {@link #rememberHostsOnMouseRelocate}
     */
    public static void preservePreviousHostAsHuntedTargetIfNeeded(PhysicalCard utinni, PhysicalCard previousHost, GameState gameState) {
        rememberHostsOnMouseRelocate(utinni, previousHost, gameState);
    }

    /**
     * Clears the Mouse-carry effect-subject marker (EFFECT_TARGET_1) after the Utinni leaves the mouse.
     * Does not clear UTINNI_EFFECT_TARGET hunt data.
     */
    public static void clearMouseCarryEffectSubject(PhysicalCard utinni) {
        if (!isUtinniEffect(utinni)) {
            return;
        }
        utinni.setTargetedCard(TargetId.EFFECT_TARGET_1, null, null, null);
    }

    /**
     * Game-text "attached to" / effect-subject host: while Mouse carries, prefer the remembered previous host
     * (EFFECT_TARGET_1). Otherwise the real physical attachedTo.
     * Use this instead of raw getAttachedTo() in Utinni cards that mean "the character this was deployed on".
     */
    public static PhysicalCard getEffectSubjectHost(GameState gameState, PhysicalCard utinni) {
        if (utinni == null) {
            return null;
        }
        if (isCarriedByMouseDroid(utinni) && gameState != null) {
            PhysicalCard remembered = utinni.getTargetedCard(gameState, TargetId.EFFECT_TARGET_1);
            if (remembered != null) {
                return remembered;
            }
        }
        return utinni.getAttachedTo();
    }

    /**
     * For Filters.hasAttached(utinni): while carried by Mouse —
     * 1) match remembered effect subject (previous character host) if present;
     * 2) else match hunt TargetId cards (site-hosted hasAttached+TargetId modifiers like Plastoid/Tusken).
     * Otherwise use real attachedTo.
     */
    public static boolean acceptsAsEffectHost(GameState gameState, ModifiersQuerying modifiersQuerying,
                                              PhysicalCard utinni, PhysicalCard candidate) {
        if (utinni == null || candidate == null) {
            return false;
        }
        if (isCarriedByMouseDroid(utinni)) {
            PhysicalCard subject = utinni.getTargetedCard(gameState, TargetId.EFFECT_TARGET_1);
            if (subject != null) {
                return Filters.sameCardId(subject).accepts(gameState, modifiersQuerying, candidate);
            }
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
     * Card Info: while Mouse carries an Utinni that still has hunted Utinni TargetId data or a remembered
     * effect subject, do not treat the mouse as targeted merely because the Utinni is attached.
     */
    public static boolean shouldSkipAttachmentTargetingForCardInfo(GameState gameState, PhysicalCard attachedCard, PhysicalCard host) {
        if (!isUtinniEffect(attachedCard) || !isMouseDroid(host) || gameState == null) {
            return false;
        }
        return attachedCard.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1) != null
                || attachedCard.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_2) != null
                || attachedCard.getTargetedCard(gameState, TargetId.EFFECT_TARGET_1) != null;
    }
}
