package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBombingRunBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Bomber
 */
public class Card4_173 extends AbstractStarfighter {
    public Card4_173() {
        super(Side.DARK, 0, 2, 1, null, 2, null, 2, "TIE Bomber");
        setLore("Because fleet orbital bombardment often destroyed entire cities, the double-pod TIE surface assault starfighter was developed to allow more precise and controlled targeting.");
        setGameText("Power +2 during a Bombing Run battle. Permanent pilot aboard provides ability of 1. At any time during your turn, may use 1 Force to 'defuse' (lose or return to hand) any one Orbital Mine present.");
        addIcons(Icon.DAGOBAH, Icon.PILOT);
        addModelType(ModelType.TIE_SA);
        addKeywords(Keyword.NO_HYPERDRIVE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new InBombingRunBattleCondition(self), 2));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter targetFilter = Filters.and(Filters.Orbital_Mine, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

            if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Defuse' (lose) Orbital Mine");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target Orbital Mine to 'defuse'", targetingReason, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("'Defuse' (lose) " + GameUtils.getCardLink(cardTargeted),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard cardToDefuse = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new LoseCardFromTableEffect(action, cardToDefuse));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }

            targetingReason = TargetingReason.OTHER;

            if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Defuse' (return to hand) Orbital Mine");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target Orbital Mine to 'defuse'", targetingReason, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("'Defuse' (return to hand) " + GameUtils.getCardLink(cardTargeted),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReturnCardToHandFromTableEffect(action, cardTargeted));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}
