package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Lost
 * Title: Standoff
 */
public class Card304_119 extends AbstractLostInterrupt {
    public Card304_119() {
        super(Side.LIGHT, 5, Title.Standoff, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Don't flinch");
        setGameText("Once per game, choose: During battle, use 2 Force to target your Gangster and opponent's character or equal or lesser ability present. Both targets are excluded from battle. OR Exchange two cards from hand with any one card from Force Pile; reshuffle.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.STANDOFF__EXCLUDE_CHARACTERS_OR_EXCHANGE_CARDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            if (GameConditions.isDuringBattle(game)
                    && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                Collection<PhysicalCard> gangster = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.gangster, Filters.presentInBattle, Filters.canBeTargetedBy(self)));
                List<PhysicalCard> validGangster = new ArrayList<PhysicalCard>();
                for (PhysicalCard aGangster : gangster) {
                    float ability = modifiersQuerying.getAbility(gameState, aGangster);
                    if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.not(Filters.abilityMoreThan(ability)), Filters.canBeTargetedBy(self)))) {
                        validGangster.add(aGangster);
                    }
                }
                if (!validGangster.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Exclude characters from battle");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Gangster", Filters.in(validGangster)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId1, final PhysicalCard gangster) {
                                    float ability = modifiersQuerying.getAbility(gameState, gangster);
                                    Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.not(Filters.abilityMoreThan(ability)));
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentsCharacterFilter) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsCharacter) {
                                                    action.addAnimationGroup(gangster, opponentsCharacter);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new UseForceEffect(action, playerId, 2));
                                                    // Allow response(s)
                                                    action.allowResponses("Exclude " + GameUtils.getCardLink(gangster) + " and " + GameUtils.getCardLink(opponentsCharacter) + " from battle",
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard gangsterToExclude = action.getPrimaryTargetCard(targetGroupId1);
                                                                    PhysicalCard opponentsCharacterToExclude = action.getPrimaryTargetCard(targetGroupId2);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new ExcludeFromBattleEffect(action, Arrays.asList(gangsterToExclude, opponentsCharacterToExclude)));
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

            if (GameConditions.hasInHand(game, playerId, 2, Filters.not(self))
                    && GameConditions.hasForcePile(game, playerId)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Exchange cards with card in Force Pile");
                action.setActionMsg("Exchange two cards in hand with a card in Force Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ExchangeCardsInHandWithCardInForcePileEffect(action, playerId, 2, 2, true));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}