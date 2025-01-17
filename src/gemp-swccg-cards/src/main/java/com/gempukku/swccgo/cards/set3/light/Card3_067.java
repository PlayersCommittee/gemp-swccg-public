package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Vehicle
 * Subtype: Combat
 * Title: Rogue 2
 */
public class Card3_067 extends AbstractCombatVehicle {
    public Card3_067() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 4, 5, Title.Rogue_2, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("Enclosed. First snowspeeder to be successfully adapted to Hoth's environment. Piloted by Zev Senesca. Led team in search of Captain Solo and Commander Skywalker.");
        setGameText("May add 2 pilots or passengers. Immune to attrition < 3 if Zev piloting. May add 2 to search party destiny draw if all pilots aboard are part of that search party.");
        addModelType(ModelType.T_47);
        addPersona(Persona.ROGUE2);
        addIcons(Icon.HOTH);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Zev);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Zev), 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isSearchPartyDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isAllPilotsAboardInSearchParty(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 2 to search party destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
