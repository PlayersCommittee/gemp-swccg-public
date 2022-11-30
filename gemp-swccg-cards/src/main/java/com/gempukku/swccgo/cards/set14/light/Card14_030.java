package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Royal Naboo Security Officer
 */
public class Card14_030 extends AbstractRepublic {
    public Card14_030() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, Title.Royal_Naboo_Security_Officer, Uniqueness.UNRESTRICTED, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("The Royal Naboo Security Forces are used to protect Naboo's planetary rulers at all times. Commanded by Panaka, these volunteers have dedicated their lives to their homeland.");
        setGameText("Power -1 while not on Naboo. While you have at least three other non-unique Royal Naboo Security Officers at related Naboo sites, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_NABOO_SECURITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(new OnCondition(self, Title.Naboo)), -1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new OnTableCondition(self, 3,
                Filters.and(Filters.your(self), Filters.other(self), Filters.non_unique, Filters.Royal_Naboo_Security_Officer,
                        Filters.at(Filters.and(Filters.relatedSite(self), Filters.Naboo_site)))), 1));
        return modifiers;
    }
}
