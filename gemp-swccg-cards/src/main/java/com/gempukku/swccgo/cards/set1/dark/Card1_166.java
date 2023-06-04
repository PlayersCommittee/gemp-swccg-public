package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Wullf Yularen
 */
public class Card1_166 extends AbstractImperial {
    public Card1_166() {
        super(Side.DARK, 2, 2, 1, 2, 5, Title.Yularen, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Imperial Security Bureau (ISB) officer assigned to brief Tarkin. Also ordered to ensure absolute loyalty to the Emperor. Leader. Will stop at nothing to fulfill the Emperor's will.");
        setGameText("Power +1 if at the same site with Tarkin, Chief Bast or General Dodonna.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.or(Filters.Tarkin, Filters.Chief_Bast, Filters.General_Dodonna)), 1));
        return modifiers;
    }
}
