package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Harc Seff
 */
public class Card7_020 extends AbstractAlien {
    public Card7_020() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, Title.Harc, Uniqueness.UNIQUE);
        setArmor(3);
        setLore("Ishi Tib accountant. Experienced administrator. Freelance consultant. Worked for many planetary governors. Knows how to get the most out of any taxation scheme.");
        setGameText("Power +1 at a swamp. Adds 2 to power of anything he pilots. While at a battleground site, whenever you must lose Force from a Force drain at an adjacent site, may reduce the loss by X by using X Force.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.ACCOUNTANT);
        setSpecies(Species.ISHI_TIB);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.swamp), 1));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.adjacentSite(self))
                && GameConditions.isAtLocation(game, self, Filters.battleground_site)) {
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
