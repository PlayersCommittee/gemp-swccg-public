package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Droid
 * Title: BG-J38
 */
public class Card6_007 extends AbstractDroid {
    public Card6_007() {
        super(Side.LIGHT, 4, 2, 1, 2, "BG-J38", Uniqueness.UNIQUE);
        setLore("Roche J9 worker drone. Undefeated hologame player. Kept by Jabba as a source of entertainment. Hoping for escape or termination.");
        setGameText("May add 2 to the destiny of each of your holograms and dejariks drawn for battle destiny or weapon destiny. While at a site you control, adds 1 to power of each of your dejariks and holograms at a holosite.");
        addModelType(ModelType.MAINTENANCE);
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if ((TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId) || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId))
                && GameConditions.isDestinyCardMatchTo(game, Filters.or(Filters.hologram, Filters.dejarik))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 2 to destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.dejarik, Filters.hologram), Filters.at(Filters.holosite)),
                new AtCondition(self, Filters.and(Filters.site, Filters.controls(self.getOwner()))), 1));
        return modifiers;
    }
}
