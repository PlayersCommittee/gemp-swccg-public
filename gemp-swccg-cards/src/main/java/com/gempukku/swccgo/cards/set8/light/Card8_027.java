package com.gempukku.swccgo.cards.set8.light;

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
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingUsingLandspeedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Sergeant Brooks Carlson
 */
public class Card8_027 extends AbstractRebel {
    public Card8_027() {
        super(Side.LIGHT, 2, 2, 3, 2, 3, "Sergeant Brooks Carlson", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Veteran pathfinder. Defected with General Madine. Recruited as a Scout for the newly-formed commando unit.");
        setGameText("Landspeed = 2. Allows your other scouts present at same exterior site to move with him (using landspeed = 2) for free. Adds 1 to immunity to attrition of all your scouts present at same exterior site who have immunity.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.your(self), Filters.scout,
                Filters.presentAt(Filters.and(Filters.sameSite(self), Filters.exterior_site)), Filters.hasAnyImmunityToAttrition), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movingUsingLandspeed(game, effectResult, self)) {
            final MovingUsingLandspeedResult movingResult = (MovingUsingLandspeedResult) effectResult;
            final PhysicalCard toLocation = movingResult.getMovingTo();
            Collection<PhysicalCard> otherScouts = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.other(self), Filters.scout, Filters.not(Filters.in(movingResult.getAllCardsMoving())),
                    Filters.presentAt(Filters.and(Filters.sameSite(self), Filters.exterior_site)), Filters.movableAsRegularMoveUsingLandspeed(playerId, movingResult.isReact(), movingResult.isMoveAway(), true, 0, 2, Filters.sameCardId(toLocation))));
            if (movingResult.isReact()) {
                otherScouts = Filters.filter(otherScouts, game, Filters.isCardEligibleToJoinMoveAsReact);
            }
            if (!otherScouts.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Move with other scouts");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Choose other scouts to move with " + GameUtils.getCardLink(self), 1, Integer.MAX_VALUE, Filters.in(otherScouts)) {
                            @Override
                            protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> targetedScouts) {
                                action.addAnimationGroup(targetedScouts);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getAppendedNames(targetedScouts) + " with " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(toLocation),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(
                                                        new AddCardsToMoveUsingLandspeedSimultaneouslyEffect(action, targetedScouts, movingResult));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
