package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Droid
 * Title: •••ID9 Probe Droid
 */
public class Card501_084 extends AbstractDroid {
    public Card501_084() {
        super(Side.DARK, 3, 1, 1, 4, "ID9 Probe Droid", Uniqueness.RESTRICTED_3);
        setArmor(4);
        setLore("Spy.");
        setGameText("During your deploy phase, may use 2 Force to relocate an Inquisitor to same site and place this droid in Used Pile. While present with Seventh Sister, opponent may not target Seventh Sister with weapons unless this card 'hit.'");
        addIcons(Icon.VIRTUAL_SET_13);
        addModelTypes(ModelType.PROBE);
        addKeyword(Keyword.SPY);
        setTestingText("•••ID9 Probe Droid");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Seventh_Sister, new AndCondition(new PresentWithCondition(self, Filters.Seventh_Sister), new UnlessCondition(new HitCondition(self)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.site)) {
            final PhysicalCard sameSite = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            Filter inquisitorFilter = Filters.and(Filters.inquisitor, Filters.canBeRelocatedToLocation(sameSite, false, true, false, 2, false));
            if (GameConditions.canTarget(game, self, inquisitorFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate Inquisitor to same site");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Inquisitor to relocate", inquisitorFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.addAnimationGroup(sameSite);
                                // Pay cost(s)
                                action.appendCost(
                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, targetedCard, sameSite, 2));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to " + GameUtils.getCardLink(sameSite),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, targetedCard, sameSite));
                                                action.appendEffect(
                                                        new PlaceCardInUsedPileFromTableEffect(action, self));
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
