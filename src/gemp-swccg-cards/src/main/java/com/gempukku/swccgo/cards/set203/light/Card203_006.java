package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Kanan Jarrus
 */
public class Card203_006 extends AbstractRebel {
    public Card203_006() {
        super(Side.LIGHT, 2, 4, 4, 5, 6, Title.Kanan, Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setLore("Padawan");
        setGameText("[Pilot] 1. While present with an Imperial (or two Rebels), all immunity to attrition here is canceled.");
        addPersona(Persona.KANAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.PADAWAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.here(self),
                new OrCondition(new PresentWithCondition(self, Filters.Imperial), new PresentWithCondition(self, 2, Filters.Rebel))));
        return modifiers;
    }
}
