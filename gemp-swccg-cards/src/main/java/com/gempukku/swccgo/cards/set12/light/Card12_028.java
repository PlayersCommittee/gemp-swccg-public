package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddDestinyDrawnToTotalPowerInBattleEffect;
import com.gempukku.swccgo.logic.effects.PreventDestinyFromBeingCanceledEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Senator Palpatine
 */
public class Card12_028 extends AbstractRepublic {
    public Card12_028() {
        super(Side.LIGHT, 3, 3, 1, 4, 5, "Senator Palpatine", Uniqueness.UNIQUE);
        setPolitics(4);
        setLore("Senator for the Naboo. Advised Amidala on actions required to highlight their conflict with the Trade Federation. Watches young Skywalker's future with great interest.");
        setGameText("Agendas: ambition, peace, order. While in a senate majority, once per turn, if opponent just drew battle destiny, use 1 Force to add its destiny number to your total power in that battle (you may not cancel that draw). Amidala is politics +1 here.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addPersona(Persona.PALPATINE);
        addKeywords(Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION, Agenda.PEACE, Agenda.ORDER));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PoliticsModifier(self, Filters.and(Filters.Amidala, Filters.here(self)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInSenateMajority(game, self)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add destiny number to power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyDrawnToTotalPowerInBattleEffect(action));
            action.appendEffect(
                    new PreventDestinyFromBeingCanceledEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
