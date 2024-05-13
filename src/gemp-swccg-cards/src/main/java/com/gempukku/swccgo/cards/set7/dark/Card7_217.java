package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedToConcealedOnlyCondition;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Special Edition
 * Type: Device
 * Title: Homing Beacon
 */
public class Card7_217 extends AbstractDevice {
    public Card7_217() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Homing Beacon", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("'You're sure the homing beacon is secure aboard their ship? I'm taking an awful risk, Vader. This had better work.'");
        setGameText("Deploy on opponent's starship (even if 'concealed') if your bounty hunter or Imperial leader is on table. Your starships may move for free (and may move as a 'react') to this location. During your move phase, may cancel Landing Claw on this starship.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.Imperial_leader)));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.starship);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.starship, self);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return SpotOverride.INCLUDE_CONCEALED;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourStarships = Filters.and(Filters.your(self), Filters.starship);
        Filter thisLocation = Filters.sameLocation(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationModifier(self, yourStarships, thisLocation));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", playerId, yourStarships, thisLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition attachedToConcealed = new AttachedToConcealedOnlyCondition(self);
        Filter yourStarships = Filters.and(Filters.your(self), Filters.starship);
        Filter thisLocation = Filters.sameLocation(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationModifier(self, yourStarships, attachedToConcealed, thisLocation));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", attachedToConcealed, playerId, yourStarships, thisLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canUseDevice(game, self, self)) {

            TopLevelGameTextAction action = getCancelLandingClawAction(playerId, game, self, gameTextSourceCardId, Filters.and(Filters.Landing_Claw, Filters.attachedTo(Filters.hasAttached(self))));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.isOnlyConcealed(game, self)
                && GameConditions.canUseDevice(game, self, self)) {

            TopLevelGameTextAction action = getCancelLandingClawAction(playerId, game, self, gameTextSourceCardId, Filters.and(Filters.Landing_Claw, Filters.attachedTo(Filters.hasAttached(self))));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private TopLevelGameTextAction getCancelLandingClawAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId, Filter landingClawFilter) {
        final Filter filter = Filters.and(Filters.Landing_Claw, Filters.attachedTo(Filters.hasAttached(self)));
        TargetingReason targetingReason = TargetingReason.TO_BE_CANCELED;
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CONCEALED, targetingReason, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel Landing Claw");
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Landing Claw", SpotOverride.INCLUDE_CONCEALED, TargetingReason.TO_BE_CANCELED, landingClawFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardOnTableEffect(action, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            return action;
        }
        return null;
    }
}