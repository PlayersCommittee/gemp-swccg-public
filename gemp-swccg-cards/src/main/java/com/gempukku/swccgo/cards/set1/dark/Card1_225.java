package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.TotalAbilityForBattleDestinyModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Molator
 */
public class Card1_225 extends AbstractNormalEffect {
    public Card1_225() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Molator, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Creature in dejarik hologame drawn from Alderaanian mythology. Stories describe molators as powerful, enchanted protectors of Alderaanian kings and queens.");
        setGameText("Deploy on your side of table. For each unit of ability you have present during a battle, you may use 1 Force to raise your total power by 1. Ability used in this way cannot also be used to draw destiny.");
        addKeywords(Keyword.DEJARIK, Keyword.HOLOGRAM);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)) {
            final PhysicalCard battleLocation = game.getGameState().getBattleState().getBattleLocation();
            float abilityPresent = game.getModifiersQuerying().getTotalAbilityPresentAtLocation(game.getGameState(), playerId, battleLocation);
            int forceAvailableToUse = GameConditions.forceAvailableToUse(game, playerId);
            final int maxForceToUse = Math.min((int) Math.floor(abilityPresent), forceAvailableToUse);
            if (maxForceToUse > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Use Force to raise total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action,playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        action.setActionMsg("Raise total power by " + result);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyTotalPowerUntilEndOfBattleEffect(action, result, playerId,
                                                        "Adds " + result + " to total power"));
                                        action.appendEffect(
                                                new AddUntilEndOfBattleModifierEffect(action,
                                                        new TotalAbilityForBattleDestinyModifier(self, battleLocation, -result, playerId), null));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}