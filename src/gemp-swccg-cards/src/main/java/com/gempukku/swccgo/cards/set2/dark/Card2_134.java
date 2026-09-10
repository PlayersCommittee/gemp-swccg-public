package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Informant
 */
public class Card2_134 extends AbstractUsedInterrupt {
    public Card2_134() {
        super(Side.DARK, 6, "Informant", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("The Empire's network of spies and petty informants allows Imperial operatives to discover and react to Rebel assaults before they occur.");
        setGameText("If a battle was just initiated at same site as your Undercover spy, your characters at adjacent sites may move there as a 'react' (for free). OR Cancel Sabotage.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.and(Filters.site, Filters.canBeTargetedBy(self)))) {
            final PhysicalCard battleSite = game.getGameState().getBattleLocation();
            // Interrupts cannot spot undercover spies via canSpot() once a battle has started
            // (GameState.iterateActiveCards special rule). filterAllOnTable includes them.
            if (battleSite != null && hasYourUndercoverSpyAt(game, self, battleSite)) {
                Filter characterFilter = getReactCharacterFilter(self, battleSite);
                if (GameConditions.canTarget(game, self, characterFilter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Move characters as 'react'");
                    // First react is required targeting so Sense/Sabotage can cancel the whole play.
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose character to move as a 'react'", characterFilter) {
                                @Override
                                protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCharacter) {
                                    action.addAnimationGroup(targetedCharacter);
                                    action.addSecondaryTargetFilter(Filters.battleLocation);
                                    // Allow response(s)
                                    action.allowResponses("Move " + GameUtils.getCardLink(targetedCharacter) + " as a 'react'",
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId1);
                                                    action.appendEffect(
                                                            new MoveAsReactEffect(action, finalCharacter, true));
                                                    // Remaining adjacent characters may optionally react for free
                                                    // as sub-actions of this same interrupt (AR Appendix C).
                                                    appendOptionalAdditionalReacts(action, playerId, self);
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Sabotage)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Sabotage)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sabotage, Title.Sabotage);
            actions.add(action);
        }
        return actions;
    }

    private boolean hasYourUndercoverSpyAt(SwccgGame game, PhysicalCard self, PhysicalCard battleSite) {
        return !Filters.filterAllOnTable(game, Filters.and(Filters.your(self), Filters.undercover_spy, Filters.at(battleSite))).isEmpty();
    }

    private Filter getReactCharacterFilter(PhysicalCard self, PhysicalCard battleSite) {
        // AR 2023: while a starship/vehicle is at a site, its sites are adjacent to that site.
        // Filters.adjacentSite already includes this via isAdjacentSites. Also union
        // siteOfStarshipOrVehicle (filterAllOnTable) so a related vehicle at the battle site
        // is found even if findFirstActive would skip it.
        Filter adjacentSites = Filters.or(
                Filters.adjacentSite(battleSite),
                Filters.siteOfStarshipOrVehicle(Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.at(battleSite))));
        return Filters.and(Filters.your(self), Filters.character, Filters.at(adjacentSites),
                Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false));
    }

    private void appendOptionalAdditionalReacts(final PlayInterruptAction action, final String playerId, final PhysicalCard self) {
        action.appendEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        PhysicalCard battleSite = game.getGameState().getBattleLocation();
                        if (battleSite == null) {
                            return;
                        }
                        Filter remaining = getReactCharacterFilter(self, battleSite);
                        if (GameConditions.canTarget(game, self, remaining)) {
                            action.appendEffect(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose another character to move as a 'react'", remaining, 0) {
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new MoveAsReactEffect(action, selectedCard, true));
                                            appendOptionalAdditionalReacts(action, playerId, self);
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }
}
