package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Character
 * Subtype: First Order
 * Title: Ap'lek
 */
public class Card222_002 extends AbstractFirstOrder {
    public Card222_002() {
        super(Side.DARK, 3, 5, 5, 3, 6, "Ap'lek", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("Knight of Ren.");
        setGameText("Adds one battle destiny. " +
                "If opponent initiates battle here, retrieve 1 Force and add its destiny number to Ap'lek's power, or power of Night Buzzard he is piloting. " +
                "End of your turn: * Use 1 or [Skull].");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.MAINTENANCE, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.KNIGHT_OF_REN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameLocation(self))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force");

            // Perform result(s)
            action.appendEffect(new AddUntilEndOfBattleActionProxyEffect(action, new AbstractActionProxy() {
                @Override
                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                    if (TriggerConditions.justLostForceFromCard(game, effectResult, opponent, self)) {
                        LostForceResult lostForceResult = (LostForceResult) effectResult;
                        final PhysicalCard lostCard = lostForceResult.getCardLost();
                        if (lostForceResult.getAmountOfForceLost() == 1
                                && lostCard != null) {
                            final GameState gameState = game.getGameState();
                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            action.appendEffect(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(lostCard)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            float destiny = modifiersQuerying.getDestiny(gameState, lostCard);
                                            if (Filters.piloting(Filters.title("Night Buzzard")).accepts(game, self)) {
                                                PhysicalCard nightBuzzard = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.title("Night Buzzard"), Filters.hasPiloting(self)));
                                                if (nightBuzzard != null) {
                                                    action.appendEffect(
                                                            new ModifyPowerEffect(action, nightBuzzard, destiny));
                                                }
                                            } else {
                                                action.appendEffect(
                                                        new ModifyPowerEffect(action, self, destiny));
                                            }
                                        }
                                    }
                            );
                        }
                    }
                    return null;
                }
            }));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 1);
    }
}
