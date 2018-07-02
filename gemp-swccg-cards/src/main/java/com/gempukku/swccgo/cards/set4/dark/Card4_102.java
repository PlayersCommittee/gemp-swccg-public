package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Helmsman
 */
public class Card4_102 extends AbstractImperial {
    public Card4_102() {
        super(Side.DARK, 3, 3, 2, 1, 2, "Imperial Helmsman", Uniqueness.RESTRICTED_3);
        setLore("Warrant Officer Bachenkall is typical of the many graduates of the Imperial Training academy on Raithal. The sector naval school trains pilots in capital starship help tactics.");
        setGameText("Adds 2 to power of anything he pilots. When piloting a Star Destroyer, also draws one battle destiny if not able to otherwise.");
        addIcons(Icon.DAGOBAH, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Star_Destroyer), 1));
        return modifiers;
    }
}
