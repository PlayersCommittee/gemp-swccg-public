package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePurchasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Droid
 * Title: IG-88
 */
public class Card4_101 extends AbstractDroid {
    public Card4_101() {
        super(Side.DARK, 1, 5, 4, 3, "IG-88", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setArmor(5);
        setLore("Bounty hunter. Went berserk upon activation. Murdered all designers at Holowan Mechanicals. IG-88's outstanding 'dismantle on sight' warrant ignored by Darth Vader.");
        setGameText("Adds 2 to power of anything he pilots. May 'assassinate' any character at same site hit by IG-88 (victim is immediately lost). May use two different weapons. May initiate battle and be battled. Immune to Restraining Bolt and purchase.");
        addPersona(Persona.IG88);
        addIcons(Icon.DAGOBAH, Icon.PILOT);
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.BOUNTY_HUNTER);
        addModelType(ModelType.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new MayNotBePurchasedModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter characterHit = Filters.and(Filters.character, Filters.atSameSite(self));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, characterHit, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, targetingReason, cardHit)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("'Assassinate' " + GameUtils.getFullName(cardHit));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", targetingReason, cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("'Assassinate' " + GameUtils.getCardLink(cardTargeted),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard cardToAssassinate = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, cardToAssassinate));
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
}
