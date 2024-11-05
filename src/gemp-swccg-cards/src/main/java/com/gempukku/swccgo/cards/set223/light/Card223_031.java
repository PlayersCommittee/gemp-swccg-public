package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Jedi's Focus (V)
 */
public class Card223_031 extends AbstractLostInterrupt {
    public Card223_031() {
        super(Side.LIGHT, 4, "A Jedi's Focus", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Vader was surprised at how far the 'young apprentice' had come in his training.");
        setGameText("If you chose I Have It on your [Skywalker] Epic Event, choose: Take any card into hand from Force Pile; reshuffle. OR Once per game during battle, if Luke present with an opponent's character of equal or lesser ability, use 2 Force to exclude them both from battle.");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final PhysicalCard skywalkerEpicEvent = Filters.findFirstActive(game, self, Filters.and(Filters.icon(Icon.SKYWALKER), Filters.Epic_Event));
        String I_HAVE_IT = "I Have It";
    
        if (skywalkerEpicEvent != null) {
            if (GameConditions.cardHasWhileInPlayDataEquals(skywalkerEpicEvent, I_HAVE_IT)) {
                
                if (GameConditions.hasForcePile(game, playerId)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Take a card from Force Pile into hand");
                    // Allow response(s)
                    action.allowResponses("Take a card from Force Pile into hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeCardIntoHandFromForcePileEffect(action,  playerId, true));
                            }
                        });
                    actions.add(action);
                }

                GameTextActionId gameTextActionId = GameTextActionId.A_JEDIS_FOCUS__EXCLUDE_CHARACTERS;

                if (GameConditions.isOncePerGame(game, skywalkerEpicEvent, gameTextActionId)
                        && GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                        && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {

                    final PhysicalCard Luke = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Luke, Filters.presentInBattle, Filters.canBeTargetedBy(self)));

                    if (Luke != null) {
                        final GameState gameState = game.getGameState();
                        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        final float ability = modifiersQuerying.getAbility(gameState, Luke);
                        final Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.abilityLessThanOrEqualTo(ability));


                        if (GameConditions.canSpot(game, self, opponentsCharacterFilter)) {
                            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                            action.setText("Exclude characters from battle");
                            // Update usage limit(s)
                            action.appendUsage(
                                    new OncePerGameEffect(action));
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentsCharacterFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, final PhysicalCard opponentsCharacter) {
                                            action.addAnimationGroup(Luke, opponentsCharacter);

                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 2));
                                            // Allow response(s)
                                            action.allowResponses("Exclude " + GameUtils.getCardLink(Luke) + " and " + GameUtils.getCardLink(opponentsCharacter) + " from battle",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            PhysicalCard opponentsCharacterToExclude = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ExcludeFromBattleEffect(action, Arrays.asList(Luke, opponentsCharacterToExclude)));
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
            }
        }
        
        return actions;
    }
}
