package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentWithEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: General Tagge
 */
public class Card1_178 extends AbstractImperial {
    public Card1_178() {
        super(Side.DARK, 1, 3, 3, 3, 4, Title.Tagge, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Oversees defense operations of Death Star. Outstanding tactician. No-nonsense leader. Member of the House of Tagge, a powerful noble family and corporate conglomerate.");
        setGameText("Tagge's forfeit +1 for each Imperial trooper, of any kind, present with him at a site.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, new AtCondition(self, Filters.site), new PresentWithEvaluator(self, Filters.and(Filters.Imperial, Filters.trooper))));
        return modifiers;
    }
}
