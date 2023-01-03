package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerAndAttritionEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Character
 * Subtype: Alien
 * Title: Dash Rendar (V)
 */
public class Card210_012 extends AbstractAlien {
    public Card210_012() {
        super(Side.LIGHT, 3, 3, 3, 3, 5, "Dash Rendar", Uniqueness.UNIQUE, ExpansionSet.SET_10, Rarity.V);
        setLore("Emperor banished Rendar family from Coruscant. Became gambler and smuggler. Brought down AT-AT at the Battle of Hoth. Works for Rebel Alliance from time to time. Corellian.");
        setGameText("[Pilot] 3. May move as a 'react'. When in battle with Leebo (or opponent's gangster, [Permanent Weapon] card, or [Maintenance] card), may draw one destiny; subtract that amount from opponent's total power and attrition.");
        addPersona(Persona.DASH);
        addIcons(Icon.VIRTUAL_SET_10, Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Outrider);
        setVirtualSuffix(true);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && (GameConditions.isInBattleWith(game, self, Filters.Leebo) || GameConditions.isInBattleWith(game, self, Filters.and(Filters.opponents(self), Filters.or(Filters.gangster, Icon.MAINTENANCE, Icon.PERMANENT_WEAPON))))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reduce opponent's attrition and total power");
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId, 1, DestinyType.DESTINY_TO_REDUCE_ATTRITION_POWER) {
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
