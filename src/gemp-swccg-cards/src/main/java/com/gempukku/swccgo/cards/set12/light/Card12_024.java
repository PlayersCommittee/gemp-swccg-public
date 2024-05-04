package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Ric Olie
 */
public class Card12_024 extends AbstractRepublic {
    public Card12_024() {
        super(Side.LIGHT, 2, 3, 3, 3, 6, "Ric Olie", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Leader of Bravo Squadron. Piloted Amidala's Royal Starship from Naboo so that she could plead her case to the Senate. Was able to break the blockade with a little help.");
        setGameText("Adds 3 to power of anything he pilots (or adds 4 if Queen's Royal Starship). While piloting Queen's Royal Starship, once during a battle Ric is in, may use X Force to reduce attrition against you by X.");
        addPersona(Persona.RIC);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.BRAVO_SQUADRON);
        setMatchingStarshipFilter(Filters.Queens_Royal_Starship);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(3, 4, Filters.Queens_Royal_Starship)));
        return modifiers;
    }

    @Override
    public List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isPiloting(game, self, Filters.Queens_Royal_Starship)
                && GameConditions.canModifyAttritionAgainst(game, playerId)) {
            int maxForceToUse = Math.min(GameConditions.forceAvailableToUse(game, playerId), (int) Math.ceil(GameConditions.getAttritionRemaining(game, playerId)));
            if (maxForceToUse > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce attrition");
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        action.setActionMsg("Reduce attrition by " + result);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ReduceAttritionEffect(action, playerId, result));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
