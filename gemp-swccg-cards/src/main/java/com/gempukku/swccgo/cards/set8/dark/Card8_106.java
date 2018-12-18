package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Major Marquand
 */
public class Card8_106 extends AbstractImperial {
    public Card8_106() {
        super(Side.DARK, 2, 2, 3, 2, 3, Title.Marquand, Uniqueness.UNIQUE);
        setLore("Directed a counterattack against the Ewoks on Endor. Formerly crewed an AT-AT assigned to Devastator. Originally from Kessel.");
        setGameText("Adds 3 to power of any combat vehicle he pilots. When piloting a combat vehicle with Watts, may add one battle destiny and, if that vehicle has immunity to attrition, adds 2 to immunity.");
        addIcons(Icon.ENDOR, Icon.PILOT);
        addPersona(Persona.MARQUAND);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.combat_vehicle));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.combat_vehicle, Filters.hasPiloting(self)),
                new PilotingCondition(self, Filters.and(Filters.combat_vehicle, Filters.hasAnyImmunityToAttrition,
                        Filters.hasPiloting(self, Filters.Watts))), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isPiloting(game, self, Filters.and(Filters.combat_vehicle, Filters.hasPiloting(self, Filters.Watts)))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
