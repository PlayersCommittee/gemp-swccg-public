package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Republic
 * Title: CC-2237 (Odd Ball)
 */
public class Card208_003 extends AbstractRepublic {
    public Card208_003() {
        super(Side.LIGHT, 3, 3, 3, 2, 4, "CC-2237 (Odd Ball)", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Clone trooper commander.");
        setGameText("[Pilot] 2. Droid starfighters are power and forfeit -1 here. During battle, may add or subtract 1 from a just drawn even destiny.");
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_8);
        addPersona(Persona.ODD_BALL);
        addKeywords(Keyword.CLONE_TROOPER, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter droidStarfightersHere = Filters.and(Filters.droid_starfighter, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, droidStarfightersHere, -1));
        modifiers.add(new ForfeitModifier(self, droidStarfightersHere, -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDestinyValueEven(game)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            actions.add(action);

            action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Subtract 1 from destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -1));
            actions.add(action);
        }
        return actions;
    }
}
