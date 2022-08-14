package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Device
 * Title: Remote
 */
public class Card2_028 extends AbstractDevice {
    public Card2_028() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_LOCATION, "Remote");
        setLore("Tshhhh. Tshhhh. Zap! 'Look...good against remotes is one thing. Good against the living, that's something else.'");
        setGameText("Use 1 Force to deploy at any site. Moves like a character at normal use of the Force. Once during each of your control phases, may: Select one character present to be power or forfeit +1 for remainder of turn. OR Use 2 Force to cancel any seeker present.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    public boolean isMovesLikeCharacter() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter characterFilter = Filters.and(Filters.character, Filters.present(self));
        Filter seekerFilter = Filters.and(Filters.seeker, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            if (GameConditions.canTarget(game, self, characterFilter)) {

                final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action1.setText("Make a character power +1");
                // Update usage limit(s)
                action1.appendUsage(
                        new OncePerPhaseEffect(action1));
                // Choose target(s)
                action1.appendTargeting(
                        new TargetCardOnTableEffect(action1, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard character) {
                                action1.addAnimationGroup(character);
                                // Allow response(s)
                                action1.allowResponses("Make " + GameUtils.getCardLink(character) + " power +1",
                                        new UnrespondableEffect(action1) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action1.appendEffect(
                                                        new ModifyPowerUntilEndOfTurnEffect(action1, character, 1));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action1);

                final TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action2.setText("Make a character forfeit +1");
                // Update usage limit(s)
                action2.appendUsage(
                        new OncePerPhaseEffect(action2));
                // Choose target(s)
                action2.appendTargeting(
                        new TargetCardOnTableEffect(action2, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard character) {
                                action2.addAnimationGroup(character);
                                // Allow response(s)
                                action2.allowResponses("Make " + GameUtils.getCardLink(character) + " forfeit +1",
                                        new UnrespondableEffect(action2) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action2.appendEffect(
                                                        new ModifyPowerUntilEndOfTurnEffect(action2, character, 1));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action2);
            }

            TargetingReason targetingReason = TargetingReason.TO_BE_CANCELED;

            if (GameConditions.canUseForce(game, playerId, 2)
                    && GameConditions.canTarget(game, self, targetingReason, seekerFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Cancel a seeker");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose seeker", targetingReason, seekerFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard seeker) {
                                action.addAnimationGroup(seeker);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 2));
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(seeker),
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalSeeker = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelCardOnTableEffect(action, finalSeeker));
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