package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Tey How
 */
public class Card12_121 extends AbstractRepublic {
    public Card12_121() {
        super(Side.DARK, 2, 2, 1, 2, 3, "Tey How", Uniqueness.UNIQUE);
        setLore("Neimoidian Trade Federation communications officer to Nute Gunray. Had audio and visual mechanics surgically implanted to assist her in shipboard operations.");
        setGameText("Adds 2 to the power of anything she pilots. While aboard a battleship, whenever you must lose Force from a Force drain at a system within 2 parsecs of How, may use X Force to reduce the loss by X.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.and(Filters.system, Filters.withinParsecsOf(self, 2)))
                && GameConditions.isAboard(game, self, Filters.battleship)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            int maxForceToUse = Math.min(GameConditions.forceAvailableToUse(game, playerId), (int) Math.ceil(result.getForceLossAmount(game)));
            if (maxForceToUse > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce Force loss");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        action.setActionMsg("Reduce Force loss by " + result);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ReduceForceLossEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
