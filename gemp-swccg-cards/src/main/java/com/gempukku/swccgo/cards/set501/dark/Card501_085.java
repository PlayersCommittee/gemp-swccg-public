package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * SubType: Sith
 * Title: Ochi
 */
public class Card501_085 extends AbstractSith {
    public Card501_085() {
        super(Side.DARK, 2, 4, 3, 4, 5, "Ochi", Uniqueness.UNIQUE);
        setLore("Assassin.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Bestoon Legacy, it is immune to attrition < 5. " +
                "While [Episode VII] Emperor on table, your total power here is +3. If opponent's character was just placed out of play, opponent loses 1 Force.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_14);
        addPersona(Persona.OCHI);
        addKeywords(Keyword.ASSASSIN);
        setMatchingStarshipFilter(Filters.Bestoon_Legacy);
        setTestingText("Ochi");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Bestoon_Legacy, new PilotingCondition(self, Filters.Bestoon_Legacy), 5));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new OnTableCondition(self, Filters.and(Filters.Emperor, Filters.icon(Icon.EPISODE_VII))), 3, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justPlacedOutOfPlay(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 force");
            action.setActionMsg("Opponent loses 1 force");
            // Update usage limit(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
