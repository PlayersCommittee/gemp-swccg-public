package com.gempukku.swccgo.cards.set200.dark;

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
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Lost
 * Title: Force Push (V)
 */
public class Card200_120 extends AbstractLostInterrupt {
    public Card200_120() {
        super(Side.DARK, 5, Title.Force_Push, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("A fully-trained Sith warrior has more weapons at his disposal than just a lightsaber.");
        setGameText("Once per game, choose: During battle, use 2 Force to target your Dark Jedi and opponent's character or equal or lesser ability present. Both targets are excluded from battle. OR Exchange two cards from hand with any one card from Force Pile; reshuffle.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.FORCE_PUSH__EXCLUDE_CHARACTERS_OR_EXCHANGE_CARDS;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            if (GameConditions.isDuringBattle(game)
                    && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
                final GameState gameState = game.getGameState();
                final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                Collection<PhysicalCard> darkJedi = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.Dark_Jedi, Filters.presentInBattle, Filters.canBeTargetedBy(self)));
                List<PhysicalCard> validDarkJedi = new ArrayList<PhysicalCard>();
                for (PhysicalCard aDarkJedi : darkJedi) {
                    float ability = modifiersQuerying.getAbility(gameState, aDarkJedi);
                    if (GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.not(Filters.abilityMoreThan(ability)), Filters.canBeTargetedBy(self)))) {
                        validDarkJedi.add(aDarkJedi);
                    }
                }
                if (!validDarkJedi.isEmpty()) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Exclude characters from battle");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", Filters.in(validDarkJedi)) {
                                @Override
                                protected void cardTargeted(final int targetGroupId1, final PhysicalCard darkJedi) {
                                    float ability = modifiersQuerying.getAbility(gameState, darkJedi);
                                    Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.not(Filters.abilityMoreThan(ability)));
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentsCharacterFilter) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsCharacter) {
                                                    action.addAnimationGroup(darkJedi, opponentsCharacter);
                                                    // Pay cost(s)
                                                    action.appendCost(
                                                            new UseForceEffect(action, playerId, 2));
                                                    // Allow response(s)
                                                    action.allowResponses("Exclude " + GameUtils.getCardLink(darkJedi) + " and " + GameUtils.getCardLink(opponentsCharacter) + " from battle",
                                                            new RespondablePlayCardEffect(action) {
                                                                @Override
                                                                protected void performActionResults(Action targetingAction) {
                                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                                    PhysicalCard darkJediToExclude = action.getPrimaryTargetCard(targetGroupId1);
                                                                    PhysicalCard opponentsCharacterToExclude = action.getPrimaryTargetCard(targetGroupId2);

                                                                    // Perform result(s)
                                                                    action.appendEffect(
                                                                            new ExcludeFromBattleEffect(action, Arrays.asList(darkJediToExclude, opponentsCharacterToExclude)));
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