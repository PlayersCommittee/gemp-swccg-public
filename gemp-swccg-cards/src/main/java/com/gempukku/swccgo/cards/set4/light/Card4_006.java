package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.CloseSpaceSlugMouthEffect;
import com.gempukku.swccgo.logic.effects.OpenSpaceSlugMouthEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.MayAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Space Slug
 */
public class Card4_006 extends AbstractCreature {
    public Card4_006() {
        super(Side.LIGHT, 4, 2, null, 3, 0, Title.Space_Slug, Uniqueness.DIAMOND_1);
        setLore("Immense spaceborne predator. Consumes minerals, mynocks and the occasional passing starfighter. Lives in asteroid caves. Only one meter long at birth.");
        setGameText("* Ferocity = two destiny. Habitat: Big One (Cave is now Belly). Attacks starfighters (defeated cards are eaten or relocated to Belly, opponent of victim chooses). Once per turn, may open or close mouth.");
        addModelType(ModelType.SPACE);
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Big_One;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 2));
        modifiers.add(new MayAttackTargetModifier(self, Filters.starfighter));
        modifiers.add(new MayNotAttackTargetModifier(self, Filters.not(Filters.starfighter)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            if (self.isMouthClosed()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Open mouth");
                action.setActionMsg("Open " + GameUtils.getCardLink(self) + "'s mouth");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new OpenSpaceSlugMouthEffect(action, self));
                return Collections.singletonList(action);
            }
            else {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Close mouth");
                action.setActionMsg("Close " + GameUtils.getCardLink(self) + "'s mouth");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new CloseSpaceSlugMouthEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDefeatedBy(game, effectResult, Filters.any, self)) {
            final PhysicalCard belly = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Space_Slug_Belly, Filters.relatedSite(self)));
            if (belly != null) {
                final PhysicalCard victim = ((DefeatedResult) effectResult).getCardDefeated();
                if (Filters.canBeRelocatedToLocation(belly, true, false, true, 0).accepts(game, victim)) {
                    final String opponentOfVictim = game.getOpponent(victim.getOwner());

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.skipInitialMessageAndAnimation();
                    action.setText("Choose whether to relocate defeated card");
                    action.setActionMsg(null);
                    // Perform result(s)
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, opponentOfVictim,
                                    new YesNoDecision("Do you want " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(belly) + " instead of eaten?") {
                                        @Override
                                        protected void yes() {
                                            game.getGameState().sendMessage(opponentOfVictim + " chooses to have " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(belly));
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, victim, belly));
                                        }

                                        @Override
                                        protected void no() {
                                            game.getGameState().sendMessage(opponentOfVictim + " chooses to not have " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(belly));
                                        }
                                    }
                            )
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
