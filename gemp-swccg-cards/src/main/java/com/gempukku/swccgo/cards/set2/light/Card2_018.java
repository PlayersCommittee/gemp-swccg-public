package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddCardsToMoveUsingLandspeedSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Squad Leader
 */
public class Card2_018 extends AbstractRebel {
    public Card2_018() {
        super(Side.LIGHT, 1, 2, 1, 1, 3, "Rebel Squad Leader", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C3);
        setLore("Lt. Pello Scrambas is a typical veteran officer in the Rebel forces. Loyally served the Organa family for nearly two decades as a guard for the Royal House of Alderaan.");
        setGameText("Adds 1 to forfeit of your other troopers and Rebel Guards at same site. When moving with a 'squad' of exactly three other troopers and/or Rebel Guards, all four move for 1 Force. Rebel Guards at same site may move.");
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR);
        addKeywords(Keyword.TROOPER, Keyword.LEADER, Keyword.GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.other(self), Filters.trooper), Filters.Rebel_Guard), Filters.atSameSite(self)), 1));
        modifiers.add(new MayMoveModifier(self, Filters.and(Filters.Rebel_Guard, Filters.atSameSite(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movingUsingLandspeed(game, effectResult, self)) {
            final MovingUsingLandspeedResult movingResult = (MovingUsingLandspeedResult) effectResult;
            final PhysicalCard toLocation = movingResult.getMovingTo();
            if (!Filters.canSpot(movingResult.getAllCardsMoving(), game, Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.other(self), Filters.trooper), Filters.Rebel_Guard)))) {
                Collection<PhysicalCard> otherTroopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.other(self), Filters.trooper), Filters.Rebel_Guard), Filters.not(Filters.in(movingResult.getAllCardsMoving())),
                        Filters.present(self), Filters.movableAsRegularMoveUsingLandspeed(playerId, movingResult.isReact(), movingResult.isMoveAway(), true, 0, null, Filters.sameCardId(toLocation))));
                if (movingResult.isReact()) {
                    otherTroopers = Filters.filter(otherTroopers, game, Filters.isCardEligibleToJoinMoveAsReact);
                }
                if (otherTroopers.size() >= 3) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Move with 'squad'");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardsOnTableEffect(action, playerId, "Choose other troopers and/or Rebel Guards to move as 'squad'", 3, 3, Filters.in(otherTroopers)) {
                                @Override
                                protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> targetedTroopers) {
                                    action.addAnimationGroup(targetedTroopers);
                                    // Allow response(s)
                                    action.allowResponses("Move " + GameUtils.getCardLink(self) + " with 'squad' of " + GameUtils.getAppendedNames(targetedTroopers) + " to " + GameUtils.getCardLink(toLocation),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    action.appendEffect(
                                                            new AddCardsToMoveUsingLandspeedSimultaneouslyEffect(action, targetedTroopers, movingResult));
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
        return null;
    }
}
