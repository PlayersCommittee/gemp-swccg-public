package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DoubledCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Vul Tazaene
 */
public class Card6_045 extends AbstractAlien {
    public Card6_045() {
        super(Side.LIGHT, 2, 2, 2, 2, 2, Title.Vul_Tazaene, Uniqueness.UNIQUE);
        setLore("Security officer from Kiffex searching for the Tonnika sisters. In love with one of them, he's not sure which.");
        setGameText("Adds 2 to power of anything he pilots. Twice during battle at same system, may use 2 Force to add 2 to any destiny of 2. If present with Tonnika Sisters, Vul and Tonnika Sisters are lost.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addPersona(Persona.VUL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 4, new DoubledCondition(self))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
            && GameConditions.canSpot(game, self, Filters.and(Filters.Tonnika_Sisters, Filters.presentWith(self)))
                && GameConditions.canSpot(game, self, Filters.Vul_Tazaene)) {

            PhysicalCard tonnikaSisters = Filters.findFirstActive(game, self, Filters.and(Filters.Tonnika_Sisters, Filters.presentWith(self)));
            PhysicalCard vul = Filters.findFirstActive(game, self, Filters.Vul_Tazaene);
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make Vul and Tonnika Sisters lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(vul) + " and " + GameUtils.getCardLink(tonnikaSisters) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardsFromTableEffect(action, Arrays.asList(vul, tonnikaSisters)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        int numberOnCard = game.getModifiersQuerying().isDoubled(game.getGameState(), self) ? 4 : 2;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.sameSystem(self))
                && GameConditions.isNumTimesPerBattle(game, self, playerId, numberOnCard, gameTextSourceCardId)
                && GameConditions.isDestinyValueEqualTo(game, numberOnCard)
                && GameConditions.canUseForce(game, playerId, numberOnCard)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add " + numberOnCard + " to destiny");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, numberOnCard));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, numberOnCard));
            return Collections.singletonList(action);
        }
        return null;
    }
}
