package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Toryn Farr (V)
 */
public class Card203_012 extends AbstractRebel {
    public Card203_012() {
        super(Side.LIGHT, 4, 2, 1, 2, 3, "Toryn Farr", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Chief Controller at Echo Command. Responsible for communicating orders to the troops. Personally gives firing orders to Ion Cannon Control.");
        setGameText("[Pilot] 2. Your total battle destiny here is +1 (+2 if piloting a transport). Once per turn, if a battle just ended here, may 'rescue' (retrieve from Lost Pile) OR stack on [Set 0] Evacuation Control) a Rebel of ability < 3 forfeited from same location this turn.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new ConditionEvaluator(1, 2, new PilotingCondition(self, Filters.transport)), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        GameTextActionId gameTextActionId = GameTextActionId.TORYN_FARR__RESCUE_REBEL;

        Filter rebelFilter = Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityLessThan(3));

        // Check condition(s)
        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.sameLocation(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.wasForfeitedFromLocationThisTurn(game, rebelFilter, Filters.sameLocation(self))) {

            final Filter forfeitedRebelFilter = Filters.and(rebelFilter, Filters.forfeitedFromLocationThisTurn(Filters.sameLocation(self)));

            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action1.setText("Retrieve forfeited Rebel");
            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerTurnEffect(action1));
            // Perform result(s)
            action1.appendEffect(
                    new RetrieveCardEffect(action1, playerId, forfeitedRebelFilter));
            actions.add(action1);

            Filter evacuationControlFilter = Filters.and(Filters.Evacuation_Control, Icon.VIRTUAL_SET_0);

            if (GameConditions.canSpot(game, self, evacuationControlFilter)) {

                final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action2.setText("Stack forfeited Rebel on Evacuation Control");
                // Update usage limit(s)
                action2.appendUsage(
                        new OncePerTurnEffect(action2));
                // Choose target(s)
                action2.appendTargeting(
                        new ChooseCardOnTableEffect(action2, playerId, "Choose Evacuation Control", evacuationControlFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard evacuationControl) {
                                // Perform result(s)
                                action2.appendEffect(
                                        new ChooseCardFromLostPileEffect(action2, playerId, forfeitedRebelFilter) {
                                            @Override
                                            protected void cardSelected(SwccgGame game, PhysicalCard rebel) {
                                                action2.appendEffect(
                                                        new StackOneCardFromLostPileEffect(action2, rebel, evacuationControl, false, false, false));
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                actions.add(action2);
            }
        }
        return actions;
    }
}
