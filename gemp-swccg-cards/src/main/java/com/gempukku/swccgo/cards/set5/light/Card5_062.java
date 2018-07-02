package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Path Of Least Resistance
 */
public class Card5_062 extends AbstractLostInterrupt {
    public Card5_062() {
        super(Side.LIGHT, 5, Title.Path_Of_Least_Resistance, Uniqueness.UNIQUE);
        setLore("Yoda didn't say anything bad about taking the 'quick and easy corridor.'");
        setGameText("Cancel Rite of Passage. OR Relocate one of your characters at an interior mobile site to a related interior mobile site.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Rite_Of_Passage)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rite_Of_Passage)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rite_Of_Passage, Title.Rite_Of_Passage);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canSpotLocation(game, 2, Filters.interior_mobile_site)) {
            Collection<PhysicalCard> characters = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.interior_mobile_site), Filters.canBeTargetedBy(self)));
            if (!characters.isEmpty()) {
                // Figure out which characters can be relocated to an adjacent site
                List<PhysicalCard> validCharacters = new LinkedList<PhysicalCard>();
                for (PhysicalCard character : characters) {
                    if (Filters.canBeRelocatedToLocation(Filters.and(Filters.interior_mobile_site, Filters.relatedSite(character)), 0).accepts(game, character)) {
                        validCharacters.add(character);
                    }
                }
                if (!validCharacters.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Relocate characters to related site");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(validCharacters)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard characterToRelocate) {
                                    Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game,
                                            Filters.and(Filters.and(Filters.interior_mobile_site, Filters.relatedSite(characterToRelocate)), Filters.locationCanBeRelocatedTo(characterToRelocate, 0)));
                                    action.appendTargeting(
                                            new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterToRelocate) + " to", Filters.in(otherSites)) {
                                                @Override
                                                protected void cardSelected(final PhysicalCard siteSelected) {
                                                    action.addAnimationGroup(characterToRelocate);
                                                    action.addAnimationGroup(siteSelected);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, characterToRelocate, siteSelected, 0));
                                                    // Allow response(s)
                                                    action.allowResponses("Relocate " + GameUtils.getCardLink(characterToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new RelocateBetweenLocationsEffect(action, finalCharacter, siteSelected));
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
            }
        }
        return actions;
    }
}