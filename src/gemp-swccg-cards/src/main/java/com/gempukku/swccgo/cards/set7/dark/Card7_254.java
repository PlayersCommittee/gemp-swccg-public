package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TractorBeamAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseTractorBeamEffect;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalTractorBeamDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: In Range
 */
public class Card7_254 extends AbstractUsedInterrupt {
    public Card7_254() {
        super(Side.DARK, 6, "In Range", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("'They'll be in range of our tractor beam in moments, my lord.' 'Good. Prepare the boarding party and set your weapons for stun.'");
        setGameText("If you have a Star Destroyer in a battle, during the weapons segment use its tractor beam for free. Add 2 to tractor beam destiny if targeting a unique (*) starship. If not captured, target is power and maneuver -3 for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game,
                Filters.and(Filters.your(playerId), Filters.Star_Destroyer, Filters.hasAttached(Filters.tractor_beam)))
        ) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Use tractor beam");
            // Allow response(s)
            Filter tractorBeamFilter = Filters.and(Filters.tractor_beam, Filters.attachedTo(Filters.and(Filters.participatingInBattle, Filters.your(playerId), Filters.Star_Destroyer)));
            TargetingReason targetingReason = TargetingReason.OTHER;
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose tractor beam to use", targetingReason, tractorBeamFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)

                            TractorBeamAction tractorBeamAction = targetedCard.getBlueprint().getTractorBeamAction(game, targetedCard);
                            Filter targetFilter = tractorBeamAction.getPossibleTargets();

                            TargetingReason targetingReason2 = TargetingReason.TO_BE_CAPTURED;

                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Target with tractor beam", targetingReason2, targetFilter) {
                                        @Override
                                        protected void cardTargeted(final int starshipTargetGroupId, final PhysicalCard targetedCard) {
                                            action.allowResponses("Use tractor beam",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            PhysicalCard finalTractorBeam = action.getPrimaryTargetCard(targetGroupId);
                                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(starshipTargetGroupId);

                                                            TotalTractorBeamDestinyModifier destinyModifier = new TotalTractorBeamDestinyModifier(self, finalTractorBeam, 2);
                                                            List<Modifier> modifiers = new LinkedList<Modifier>();
                                                            modifiers.add(new PowerModifier(self, Filters.none, -3));
                                                            modifiers.add(new ManeuverModifier(self, Filters.none, -3));

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new UseTractorBeamEffect(action, finalTractorBeam, true, Filters.unique, destinyModifier, modifiers, finalTarget));
                                                        }
                                                    });
                                        }
                                    }
                            );

                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}