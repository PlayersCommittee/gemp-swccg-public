package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Tauntaun Handler
 */
public class Card3_021 extends AbstractRebel {
    public Card3_021() {
        super(Side.LIGHT, 2, 2, 1, 1, 3, "Tauntaun Handler", Uniqueness.RESTRICTED_3, ExpansionSet.HOTH, Rarity.C2);
        setLore("Corman Quien is a typical scout at Echo Base. Captured, tamed and trained the native tauntauns.");
        setGameText("Adds 2 to power of any creature vehicle he rides. When riding a tauntaun, also draws one battle destiny if not able to otherwise.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.creature_vehicle, Filters.hasAboard(self)), 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AboardCondition(self, Filters.tauntaun), 1));
        return modifiers;
    }
}
