package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddCardsToMoveUsingLandspeedSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10
 * Type: Character
 * Subtype: Republic Character
 * Title: Clone Squad Leader
 */

public class Card210_008 extends AbstractRepublic {
    public Card210_008() {
        super(Side.LIGHT, 3, 3, 3, 2, 4, "Clone Squad Leader", Uniqueness.RESTRICTED_3);
        setArmor(4);
        setLore("Clone trooper.");
        setGameText("Your other clones present are each forfeit +1. Once during battle with your clone, may cancel and redraw your just drawn battle destiny. When moving with a 'squad' of up to three other clones, they all move simultaneously for 1 Force.");
        addIcons(Icon.WARRIOR, Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.LEADER, Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Targets: Your, Clone, Present with this card, not this card. (Should it be Filters.your(self.getOwner)?)
        Filter targets = Filters.and(Filters.your(self), Filters.clone, Filters.presentWith(self), Filters.not(self));
        modifiers.add(new ForfeitModifier(self, targets, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Movement related stuff, reference Rebel Squad Leader
        // Check condition(s)
        if (TriggerConditions.movingUsingLandspeed(game, effectResult, self)) {
            final MovingUsingLandspeedResult movingResult = (MovingUsingLandspeedResult) effectResult;
            final PhysicalCard toLocation = movingResult.getMovingTo();
            if (!Filters.canSpot(movingResult.getAllCardsMoving(), game, Filters.and(Filters.your(self), Filters.and(Filters.other(self), Filters.clone)))) {
                Collection<PhysicalCard> otherClones = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.and(Filters.other(self), Filters.clone), Filters.not(Filters.in(movingResult.getAllCardsMoving())),
                        Filters.present(self), Filters.movableAsRegularMoveUsingLandspeed(playerId, movingResult.isReact(), movingResult.isMoveAway(), true, 0, null, Filters.sameCardId(toLocation))));
                if (movingResult.isReact()) {
                    otherClones = Filters.filter(otherClones, game, Filters.isCardEligibleToJoinMoveAsReact);
                }
                if (otherClones.size() >= 1) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Move with 'squad'");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardsOnTableEffect(action, playerId, "Choose other clones to move as 'squad'", 1, 3, Filters.in(otherClones)) {
                                @Override
                                protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> targetedClones) {
                                    action.addAnimationGroup(targetedClones);
                                    // Allow response(s)
                                    action.allowResponses("Move " + GameUtils.getCardLink(self) + " with 'squad' of " + GameUtils.getAppendedNames(targetedClones) + " to " + GameUtils.getCardLink(toLocation),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    action.appendEffect(
                                                            new AddCardsToMoveUsingLandspeedSimultaneouslyEffect(action, targetedClones, movingResult));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }

        // Destiny draw stuff
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, self.getOwner())
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isInBattleWith(game, self, Filters.and(Filters.your(self.getOwner()), Filters.clone)))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }

        return null;
    }

}