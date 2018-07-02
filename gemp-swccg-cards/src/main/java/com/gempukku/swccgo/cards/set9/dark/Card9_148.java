package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Location
 * Subtype: System
 * Title: Gall
 */
public class Card9_148 extends AbstractSystem {
    public Card9_148() {
        super(Side.DARK, Title.Gall, 9);
        setLocationDarkSideGameText("While you occupy, once per battle you may choose a target just 'hit' by a blaster or rifle (except Permanent weapons) to be lost.");
        setLocationLightSideGameText("Immune to Revolution.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DEATH_STAR_II, Icon.PLANET);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, Filters.and(Filters.or(Filters.blaster, Filters.rifle),
                Filters.except(Filters.and(Icon.PERMANENT_WEAPON, Filters.weapon))))
                && GameConditions.isOncePerBattle(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId)
                && GameConditions.occupies(game, playerOnDarkSideOfLocation, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, targetingReason, cardHit)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
                action.setText("Make " + GameUtils.getFullName(cardHit) + " lost");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Target character", targetingReason, cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " lost",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard cardToMakeLost = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, cardToMakeLost));
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }
}