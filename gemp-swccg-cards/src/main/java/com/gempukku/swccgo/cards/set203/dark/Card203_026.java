package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractSith;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Sith
 * Title: Darth Maul, Lone Hunter
 */
public class Card203_026 extends AbstractSith {
    public Card203_026() {
        super(Side.DARK, 1, 7, 7, 6, 8, "Darth Maul, Lone Hunter", Uniqueness.UNIQUE);
        setLore("Trade Federation.");
        setGameText("Cancels the game text of Amidala, Obi-Wan, and Qui-Gon at same site. While alone, Blaster Deflection is canceled here and Maul's weapon destiny draws may not be modified or canceled by opponent. Immune to attrition < 5.");
        addPersona(Persona.MAUL);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition whileAlone = new AloneCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.or(Filters.Amidala, Filters.ObiWan, Filters.QuiGon), Filters.atSameSite(self))));
        modifiers.add(new MayNotModifyWeaponDestinyModifier(self, whileAlone, opponent, Filters.any, self));
        modifiers.add(new MayNotCancelWeaponDestinyModifier(self, whileAlone, opponent, Filters.any, self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Blaster_Deflection)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isAlone(game, self)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.any, Filters.here(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
