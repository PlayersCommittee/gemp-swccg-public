package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CostToDrawDestinyCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Gold Leader In Gold 1 (V)
 */
public class Card601_248 extends AbstractStarfighter {
    public Card601_248() {
        super(Side.LIGHT, 2, 3, 4, null, 3, 4, 4, "Gold Leader In Gold 1", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("At the Battle of Yavin, Dutch led his squadron of outdated but reliable Y-wings in the first wave of the assault against the Death Star.");
        setGameText("May add 1 pilot or passenger. Permanent pilot aboard is •Dutch, who provides ability of 2. Opponent may not 'react' to here and must first use 1 Force to draw a card for battle destiny here.");
        addPersonas(Persona.GOLD_1);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_1);
        addKeywords(Keyword.GOLD_SQUADRON);
        addModelType(ModelType.Y_WING);
        setPilotOrPassengerCapacity(1);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.DUTCH, 2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.here(self), opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isCheckingCostsToDrawBattleDestiny(game, effectResult, opponent)
                && GameConditions.isInBattle(game, self)) {
            final GameState gameState = game.getGameState();
            final CostToDrawDestinyCardResult result = (CostToDrawDestinyCardResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add cost to draw battle destiny");
            action.setActionMsg("Make opponent use 1 Force to draw a card for battle destiny");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            if (GameConditions.canUseForce(game, opponent, 1)) {
                                // Ask player to Use Force or card for battle destiny is not drawn
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new YesNoDecision("Do you want to use 1 Force to draw a card for battle destiny?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new UseForceEffect(action, opponent, 1));
                                                    }

                                                    @Override
                                                    protected void no() {
                                                        gameState.sendMessage(opponent + " chooses to not use 1 Force to draw a card for battle destiny");
                                                        result.costToDrawCardFailed(true);
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                gameState.sendMessage(opponent + " is unable to use 1 Force to draw a card for battle destiny");
                                result.costToDrawCardFailed(false);
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
