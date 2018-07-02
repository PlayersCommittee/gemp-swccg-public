package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.UtinniEffectCompletedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Outrider
 */
public class Card10_017 extends AbstractStarfighter {
    public Card10_017() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, Title.Outrider, Uniqueness.UNIQUE);
        setLore("Highly modified Corellian Engineering Corporation YT-2400. KonGar KGDefender military grade ion engines. Griffyn/Y2TG hyperdrive. Never boarded by Imperial customs.");
        setGameText("May add 2 pilots and 1 passenger. Utinni Effects which retrieve Force are immune to Alter, but are placed out of play when completed. While Dash or Leebo piloting, immune to attrition < 4.");
        addIcons(Icon.REFLECTIONS_II, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(1);
        setMatchingPilotFilter(Filters.or(Filters.Dash, Filters.Leebo));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Utinni_Effect_that_retrieves_Force, Title.Alter));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.or(Filters.Dash, Filters.Leebo)), 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.utinniEffectCompleted(game, effectResult, Filters.Utinni_Effect_that_retrieves_Force)) {
            PhysicalCard utinniEffect = ((UtinniEffectCompletedResult) effectResult).getUtinniEffect();
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, utinniEffect)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getFullName(utinniEffect) + " out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(utinniEffect) + " out of play");
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardOutOfPlayFromTableEffect(action, utinniEffect));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
