package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
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
 * Set: Reflections III
 * Type: Character
 * Subtype: Rebel
 * Title: Lando Calrissian, Scoundrel
 */
public class Card13_027 extends AbstractRebel {
    public Card13_027() {
        super(Side.LIGHT, 5, 5, 6, 3, 7, "Lando Calrissian, Scoundrel", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("With a silvery tongue or a rapid-fire blaster, Lando prides himself on being able to get out of tight situations. Is at his best when cornered.");
        setGameText("Adds one battle destiny. If opponent initiates battle at same location, retrieve 1 Force and add its destiny number to Lando's power, or power of Falcon he is piloting. End of your turn: Use 1 Force to maintain OR Lose 2 Force to place in Used Pile OR Place out of play.");
        addPersona(Persona.LANDO);
        addIcons(Icon.REFLECTIONS_III, Icon.PILOT, Icon.WARRIOR, Icon.MAINTENANCE);
        setMatchingStarshipFilter(Filters.Falcon);
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
                                            if (Filters.piloting(Filters.Falcon).accepts(game, self)) {
                                                PhysicalCard falcon = Filters.findFirstFromAllOnTable(game, Filters.Falcon);
                                                if (falcon != null) {
                                                    action.appendEffect(
                                                            new ModifyPowerEffect(action, falcon, destiny));
                                                }
                                            }
                                            else {
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

    @Override
    protected StandardEffect getGameTextMaintenanceRecycleCost(Action action, final String playerId) {
        return new LoseForceEffect(action, playerId, 2, true);
    }
}
