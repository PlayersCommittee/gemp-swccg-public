package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Starship Levitation
 */
public class Card4_060 extends AbstractUsedOrLostInterrupt {
    public Card4_060() {
        super(Side.LIGHT, 5, "Starship Levitation", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("A Jedi can move large objects with the power of the mind.");
        setGameText("USED: Cancel Awwww, Cannot Get Your Ship Out. LOST: Use 1 Force to draw destiny. If destiny < ability of your highest-ability character on table, search your Used Pile and take one starship into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Awwww_Cannot_Get_Your_Ship_Out)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Awwww_Cannot_Get_Your_Ship_Out)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Awwww_Cannot_Get_Your_Ship_Out, Title.Awwww_Cannot_Get_Your_Ship_Out);
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.STARSHIP_LEVITATION__UPLOAD_STARSHIP_FROM_USED_PILE;
        Filter filter = Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId));

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId, true)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpot(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take starship into hand from Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a starship into hand from Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                            return Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId)));
                                        }
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }

                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                            PhysicalCard highestAbilityCharacter = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId)));
                                            if (highestAbilityCharacter == null) {
                                                gameState.sendMessage("Result: Failed due to no highest ability character");
                                                return;
                                            }
                                            float highestAbility = modifiersQuerying.getAbility(gameState, highestAbilityCharacter);
                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(highestAbility));

                                            if (totalDestiny < highestAbility) {
                                                gameState.sendMessage("Result: Succeeded");
                                                action.appendEffect(
                                                        new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.starship, true));
                                            }
                                            else {
                                                gameState.sendMessage("Result: Failed");
                                            }
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