package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetAllCardsAtSameLocationEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Into The Garbage Chute, Flyboy
 */
public class Card1_089 extends AbstractUsedInterrupt {
    public Card1_089() {
        super(Side.LIGHT, 6, Title.Into_The_Garbage_Chute_Flyboy, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Leia led an unorthodox escape into a 'garbage masher' on the detention level. 'What an incredible smell you've discovered!'");
        setGameText("If Trash Compactor is on table, cancel any battle just initiated at another Death Star site by moving (for free) all your characters involved to the Trash Compactor.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.and(Filters.Death_Star_site, Filters.not(Filters.Trash_Compactor), Filters.canBeTargetedBy(self)))) {
            PhysicalCard trashCompactor = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Trash_Compactor, Filters.canBeTargetedBy(self)));
            if (trashCompactor != null) {
                final Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle, Filters.canBeRelocatedToLocation(trashCompactor, true, 0));
                if (GameConditions.canTarget(game, self, characterFilter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setActionMsg("Relocate characters to " + GameUtils.getCardLink(trashCompactor));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetAllCardsAtSameLocationEffect(action, playerId, "Choose characters", characterFilter) {
                                @Override
                                protected boolean getUseShortcut() {
                                    return true;
                                }
                                @Override
                                protected void cardsTargeted(final int targetGroupId1, Collection<PhysicalCard> targetedCharacters) {
                                    action.addAnimationGroup(targetedCharacters);
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose Trash Compactor", Filters.Trash_Compactor) {
                                                @Override
                                                protected boolean getUseShortcut() {
                                                    return true;
                                                }
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedTrashCompactor) {
                                                    action.addAnimationGroup(targetedTrashCompactor);
                                                    // Set secondary target filter(s)
                                                    action.addSecondaryTargetFilter(Filters.battleLocation);
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate characters to " + GameUtils.getCardLink(targetedTrashCompactor),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the final targeted card(s)
                                                                    Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId1);
                                                                    PhysicalCard finalTrashCompactor = action.getPrimaryTargetCard(targetGroupId2);
                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacters, finalTrashCompactor));
                                                                }
                                                            }
                                                    );
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