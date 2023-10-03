package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

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
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameLocation(self))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1) {
                        @Override
                        protected void cardRetrieved(final PhysicalCard retrievedCard) {
                            final GameState gameState = game.getGameState();
                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            action.appendEffect(
                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(retrievedCard)) {
                                        @Override
                                        protected void refreshedPrintedDestinyValues() {
                                            float destiny = modifiersQuerying.getDestiny(gameState, retrievedCard);
                                            if (Filters.piloting(Filters.title("Night Buzzard")).accepts(game, self)) {
                                                PhysicalCard nightBuzzard = Filters.findFirstFromAllOnTable(game, Filters.title("Night Buzzard"));
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
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 1);
    }
}
