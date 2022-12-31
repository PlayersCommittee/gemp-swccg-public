package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeTowardTargetModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Cal Alder
 */
public class Card3_002 extends AbstractRebel {
    public Card3_002() {
        super(Side.LIGHT, 2, 2, 2, 1, 3, "Cal Alder", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.U2);
        setLore("An expert Scout originally from Kai'Shebbol in the Kathol sector. Served with Bren Derlin for many years. Patrols the outer perimeter of Echo Base.");
        setGameText("Power +1 at Defensive Perimeter. Your vehicles move for free if moving toward same site as Cal.");
        addPersona(Persona.CAL);
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Defensive_Perimeter), 1));
        modifiers.add(new MovesForFreeTowardTargetModifier(self, Filters.and(Filters.your(self), Filters.vehicle, Filters.onSamePlanet(self)),
                new AtCondition(self, Filters.Cal, Filters.site), Filters.sameSite(self)));
        return modifiers;
    }
}
