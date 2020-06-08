package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Resistance
 * Title: Major Caluan Ematt
 */
public class Card207_007 extends AbstractResistance {
    public Card207_007() {
        super(Side.LIGHT, 2, 4, 4, 3, 5, "Major Caluan Ematt", Uniqueness.UNIQUE);
        setGameText("May deploy -2 as a react to an [Episode VII] site. At the end of each turn, you must use 2 Force, lose 2 Force, or return Ematt to your hand.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.and(Filters.site, Icon.EPISODE_VII), -2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            boolean useForceIsOption = GameConditions.canUseForce(game, playerId, 2);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            if (useForceIsOption)
                action.setText("Use 2 Force, lose 2 Force, or return to hand");
            else
                action.setText("Lose 2 Force or return to hand");
            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            if (useForceIsOption) {
                effectsToChoose.add(new UseForceEffect(action, playerId, 2));
            }
            effectsToChoose.add(new LoseForceEffect(action, playerId, 2, true));
            effectsToChoose.add(new ReturnCardToHandFromTableEffect(action, self));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
