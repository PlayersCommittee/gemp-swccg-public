package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Starship
 * Subtype: Capital
 * Title: Solari
 */
public class Card305_009 extends AbstractCapitalStarship {
    public Card305_009() {
        super(Side.LIGHT, 1, 12, 9, 8, null, 3, 12, "Solari", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.UR);
        setLore("The pride of Clan Odan-Urr's fleet. While originally a MC-80 star cruiser she has been retrofitted with the latest technology from Arx Fleet Systems.");
        setGameText("May add unlimited pilots, passengers, vehicles and starfighters. Has ship-docking capability. [Pilot] 4. Immune to attrition < 8 (< 10 when [COU] leader piloting). Each of it's weapon destiny draws are +2. Capital starships it hits are power -5.");
        addPersona(Persona.SOLARI);
        addIcons(Icon.COU, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(Integer.MAX_VALUE);
        setPassengerCapacity(Integer.MAX_VALUE);
        setVehicleCapacity(Integer.MAX_VALUE);
        setStarfighterCapacity(Integer.MAX_VALUE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(8, 10, new HasPilotingCondition(self, Filters.COU_leader))));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.capital_starship, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce " + GameUtils.getFullName(cardHit) + "'s power by 5");
            action.setActionMsg("Reduce " + GameUtils.getCardLink(cardHit) + "'s power by 5");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerEffect(action, cardHit, -5));
            return Collections.singletonList(action);
        }
        return null;
    }
}
