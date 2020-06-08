package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerAndAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 3
 * Type: Starship
 * Subtype: Capital
 * Title: Bright Hope (V)
 */
public class Card203_020 extends AbstractCapitalStarship {
    public Card203_020() {
        super(Side.LIGHT, 3, 3, 1, 4, null, 4, 5, "Bright Hope", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Modified medium transport. Well armored. Has expanded passenger capacity to facilitate evacuation. The last transport to escape Hoth. Nearly destroyed by the Stalker.");
        setGameText("May add 2 pilots and 6 passengers. Permanent pilot aboard provides ability of 2. If in battle with another Rebel capital starship, may draw destiny. Subtract that amount from opponent's attrition and total power.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(2);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isInBattleWith(game, self, Filters.Rebel_capital_starship)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final BattleState battleState = game.getGameState().getBattleState();
            final float currentAttrition = battleState.getAttritionTotal(game, playerId);
            final float currentPower = battleState.getTotalPower(game, game.getOpponent(playerId));
            if (currentAttrition > 0 && currentPower > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce opponent's attrition and total power");
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                if (totalDestiny != null && totalDestiny > 0) {
                                    action.appendEffect(
                                            new SubtractFromOpponentsTotalPowerAndAttritionEffect(action, totalDestiny));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
