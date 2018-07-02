package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose to either have the card to stolen and attached to a specified card
 * (if possible) or have it be lost.
 */
public class ChooseToStealAndAttachOrBeLostEffect extends AbstractSubActionEffect {
    private final String _playerToChoose;
    private PhysicalCard _cardToStealOrBeLost;
    private PhysicalCard _attachTo;

    /**
     * Creates an effect that causes the specified player to choose to either have the card to stolen and attached to a
     * specified card (if possible) or have it be lost.
     * @param action the action performing this effect
     * @param playerToChoose the player to choose
     * @param cardToStealOrBeLost the card to be stolen or lost
     * @param attachTo the card to attach the stolen card to
     */
    public ChooseToStealAndAttachOrBeLostEffect(Action action, String playerToChoose, PhysicalCard cardToStealOrBeLost, PhysicalCard attachTo) {
        super(action);
        _playerToChoose = playerToChoose;
        _cardToStealOrBeLost = cardToStealOrBeLost;
        _attachTo = attachTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        // Check if there is a valid move away action
        if (Filters.canStealAndCarry(_cardToStealOrBeLost).accepts(game, _attachTo)) {
            // It is valid for card to be stolen and attached to card, so ask player to choose
            // Perform result(s)
            subAction.appendTargeting(
                    new PlayoutDecisionEffect(subAction, _playerToChoose,
                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Steal " + GameUtils.getFullName(_cardToStealOrBeLost), "Make " + GameUtils.getFullName(_cardToStealOrBeLost) + " lost"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index==0) {
                                        TargetCardOnTableEffect targetCardOnTableEffect = new TargetCardOnTableEffect(subAction, _playerToChoose, "Choose card to steal", TargetingReason.TO_BE_STOLEN, _cardToStealOrBeLost) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                                subAction.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                subAction.allowResponses("Steal " + GameUtils.getCardLink(targetedCard),
                                                        new RespondableEffect(subAction) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                subAction.appendEffect(
                                                                        new StealCardAndAttachFromTableEffect(subAction, _cardToStealOrBeLost, _attachTo));
                                                            }
                                                        }
                                                );
                                            }
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                        };
                                        subAction.appendTargeting(targetCardOnTableEffect);
                                    }
                                    else {
                                        TargetCardOnTableEffect targetCardOnTableEffect = new TargetCardOnTableEffect(subAction, _playerToChoose, "Choose card to make lost", TargetingReason.TO_BE_LOST, _cardToStealOrBeLost) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                                subAction.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                subAction.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                                        new RespondableEffect(subAction) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                subAction.appendEffect(
                                                                        new LoseCardFromTableEffect(subAction, _cardToStealOrBeLost));
                                                            }
                                                        }
                                                );
                                            }
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                        };
                                        subAction.appendTargeting(targetCardOnTableEffect);
                                    }
                                }
                            }
                    )
            );
        }
        else {
            // Not valid to steal, so card is lost
            TargetCardOnTableEffect targetCardOnTableEffect = new TargetCardOnTableEffect(subAction, _playerToChoose, "Choose card to make lost", TargetingReason.TO_BE_LOST, _cardToStealOrBeLost) {
                @Override
                protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                    subAction.addAnimationGroup(targetedCard);
                    // Allow response(s)
                    subAction.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                            new RespondableEffect(subAction) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    subAction.appendEffect(
                                            new LoseCardFromTableEffect(subAction, _cardToStealOrBeLost));
                                }
                            }
                    );
                }
                @Override
                protected boolean getUseShortcut() {
                    return true;
                }
            };
            subAction.appendTargeting(targetCardOnTableEffect);
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
