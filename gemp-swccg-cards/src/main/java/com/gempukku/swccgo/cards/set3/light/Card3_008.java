package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DoublePowerPresentInBattleEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: EG-4 (Eegee-Four)
 */
public class Card3_008 extends AbstractDroid {
    public Card3_008() {
        super(Side.LIGHT, null, 1, 0, 3, "EG-4 (Eegee-Four)");
        setLore("Very popular droid on inhospitable worlds. Rugged design. Modified top-mounted power sockets. Donated to the Alliance by the Bothan underground.");
        setGameText("Power +1 for each of your droids present, except power droids. *Destiny equals zero, but if drawn as a battle destiny where you have less power than opponent, your total power present is doubled (once per battle).");
        addIcons(Icon.HOTH);
        addModelType(ModelType.POWER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDestinyModifier(self, 0));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.droid, Filters.except(Filters.power_droid), Filters.present(self)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId)
                && GameConditions.hasLessPowerInBattleThanOpponent(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Double total power present");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DoublePowerPresentInBattleEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
