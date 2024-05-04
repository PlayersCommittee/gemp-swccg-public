package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Interrupt
 * Subtype: Lost
 * Title: I Have You Now (V)
 */
public class Card218_011 extends AbstractLostInterrupt {
    public Card218_011() {
        super(Side.DARK, 5, Title.I_Have_You_Now, Uniqueness.UNRESTRICTED, ExpansionSet.SET_18, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Several fighters have broken off from the main group. Come with me.' Darth Vader targets his TIE fighter's fire-linked blaster cannons at the Rebel pilots in the trench.");
        setGameText("If Revenge Of The Sith on table, choose: If a Dark Jedi is in battle with an opponent's character of ability > 3, add one battle destiny. OR During your move phase, use 2 Force to relocate your apprentice from an [Episode I] site to another site.");
        addIcons(Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi)
                && GameConditions.canSpot(game, self, Filters.Revenge_Of_The_Sith)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.abilityMoreThan(3)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canSpot(game, self, Filters.Revenge_Of_The_Sith)
                && GameConditions.canTarget(game, self, Filters.and(Filters.Sith_Apprentice, Filters.at(Filters.and(Icon.EPISODE_I, Filters.site)), Filters.canBeRelocatedToLocation(Filters.any, true, 2)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate apprentice to another site");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose apprentice", Filters.and(Filters.Sith_Apprentice, Filters.at(Filters.and(Icon.EPISODE_I, Filters.site)), Filters.canBeRelocatedToLocation(Filters.any, true, 2))) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard characterToRelocate) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterToRelocate) + " to", Filters.locationCanBeRelocatedTo(characterToRelocate, true, 2)) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard siteSelected) {
                                            action.addAnimationGroup(characterToRelocate);
                                            action.addAnimationGroup(siteSelected);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, siteSelected, 2));
                                            // Allow response(s)
                                            action.allowResponses("Relocate " + GameUtils.getCardLink(characterToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);
                                                            PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new RelocateBetweenLocationsEffect(action, finalCharacter, finalSite));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}