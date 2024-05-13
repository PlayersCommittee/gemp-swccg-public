package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: Battle Droid Officer
 */
public class Card14_073 extends AbstractDroid {
    public Card14_073() {
        super(Side.DARK, 2, 3, 3, 3, "Battle Droid Officer", Uniqueness.RESTRICTED_3, ExpansionSet.THEED_PALACE, Rarity.C);
        setArmor(4);
        setLore("Leader. Officer battle droids contain programming that permits them to adapt to unusual situations. Known to malfunction from time to time.");
        setGameText("Your infantry battle droids deploy -1 to same site. While with another battle droid, adds 1 to your total battle destiny here. Your battle droids may move to this site for free.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.LEADER, Keyword.OFFICER_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.infantry_battle_droid),
                -1, Filters.sameSite(self)));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new WithCondition(self, Filters.and(Filters.other(self), Filters.battle_droid)),
                1, self.getOwner()));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.your(self), Filters.battle_droid), Filters.sameSite(self)));
        return modifiers;
    }
}
