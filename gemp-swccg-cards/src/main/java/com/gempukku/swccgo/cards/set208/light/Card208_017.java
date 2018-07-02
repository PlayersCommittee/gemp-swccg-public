package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceDownEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Epic Event
 * Title: Restore Freedom To The Galaxy
 */
public class Card208_017 extends AbstractEpicEventDeployable {
    public Card208_017() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Restore_Freedom_To_The_Galaxy, Uniqueness.UNIQUE);
        setGameText("If Yavin 4 on table, deploy on table. If you just initiated a Force drain at a battleground system (except a 'liberated' system), may draw destiny. Add 1 for each piloted unique (•) snub fighter present at that system. If total destiny > 5, opponent loses 1 Force and stacks lost card face down there (while that card stacked there, system is 'liberated'). If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile. At each 'liberated' system, your Force drains are +1 and opponent's starships are deploy +1.");
        addIcons(Icon.VIRTUAL_SET_8);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpotLocation(game, Filters.Yavin_4_system);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.and(Filters.battleground_system, Filters.except(Filters.liberated_system)))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw destiny");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new PresentEvaluator(self, system, Filters.and(Filters.snub_fighter, Filters.unique, Filters.piloted)));
                            return Collections.singletonList(modifier);
                        }
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null) {
                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }

                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 5) {
                                gameState.sendMessage("Result: Succeeded");
                                action.appendEffect(
                                        new LoseForceAndStackFaceDownEffect(action, opponent, 1, system, true));
                            }
                            else {
                                gameState.sendMessage("Result: Failed");
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.liberated_system)) {
            final PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();
            Collection<PhysicalCard> liberationCards = Filters.filterStacked(game, Filters.and(Filters.liberationCard, Filters.stackedOn(system)));

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place liberation card in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutStackedCardsInUsedPileEffect(action, opponent, liberationCards, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.liberated_system, 1, self.getOwner()));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.starship), 1, Filters.liberated_system));
        return modifiers;
    }
}