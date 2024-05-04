package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Mod Terrik
 */
public class Card221_010 extends AbstractImperial {
    public Card221_010() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Captain Mod Terrik", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setArmor(4);
        setLore("Captain Mod Terrik, a typical officer of the Desert Sands sandtrooper unit. Fearless and highly motivated. Willing to sacrifice as many troops as necessary. Leader.");
        setGameText("Power +1 if Stardust or Stolen Data Tapes on table. While with another trooper, attrition against opponent here is +1. During your move phase, when moving with a 'squad' of up to two other stormtroopers, they all move (using landspeed) for 1 Force.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.SANDTROOPER, Keyword.LEADER, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, new OnTableCondition(self, Filters.or(Filters.Stardust, Filters.Stolen_Data_Tapes)), 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new WithCondition(self, Filters.trooper), 1, opponent));

        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movingUsingLandspeed(game, effectResult, self)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)) {

            final MovingUsingLandspeedResult movingResult = (MovingUsingLandspeedResult) effectResult;
            final PhysicalCard toLocation = movingResult.getMovingTo();
            if (!Filters.canSpot(movingResult.getAllCardsMoving(), game, Filters.and(Filters.your(self), Filters.other(self), Filters.stormtrooper))) {
                Collection<PhysicalCard> otherTroopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.other(self), Filters.stormtrooper, Filters.not(Filters.in(movingResult.getAllCardsMoving())),
                        Filters.present(self), Filters.movableAsRegularMoveUsingLandspeed(playerId, movingResult.isReact(), movingResult.isMoveAway(), true, 0, null, Filters.sameCardId(toLocation))));
                if (movingResult.isReact()) {
                    otherTroopers = Filters.filter(otherTroopers, game, Filters.isCardEligibleToJoinMoveAsReact);
                }
                if (otherTroopers.size() >= 1) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Move with 'squad'");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardsOnTableEffect(action, playerId, "Choose other stormtroopers to move as 'squad'", 1, 2, Filters.in(otherTroopers)) {
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
