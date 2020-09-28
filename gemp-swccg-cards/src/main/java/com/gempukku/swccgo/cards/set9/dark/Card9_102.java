package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Sarkli
 */
public class Card9_102 extends AbstractImperial {
    public Card9_102() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Captain Sarkli", Uniqueness.UNIQUE);
        setLore("Piett's nephew. Once granted audience with Emperor. On fast-track to promotion. Absolutely fearless spy.");
        setGameText("Adds 2 to power of anything he pilots. While he controls opponent's non-battleground location, opponent generates no Force here. While he occupies opponent's Subjugated system, Liberation is flipped and Local Uprising may not be flipped.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new GenerateNoForceModifier(self, Filters.here(self), new ControlsCondition(self,
                Filters.and(Filters.opponents(self), Filters.non_battleground_location)), opponent));
        modifiers.add(new MayNotBeFlippedModifier(self, new OccupiesCondition(self, Filters.Subjugated_system), Filters.Local_Uprising));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.occupiesLocation(game, self, Filters.Subjugated_system)) {
            PhysicalCard objective = Filters.findFirstActive(game, self, Filters.Liberation);
            if (objective != null
                    && GameConditions.canBeFlipped(game, objective)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip Liberation");
                action.setActionMsg("Flip " + GameUtils.getCardLink(objective));
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, objective));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
