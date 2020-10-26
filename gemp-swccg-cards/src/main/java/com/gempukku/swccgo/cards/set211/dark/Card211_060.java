package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11
 * Type: Character
 * Subtype: Droid
 * Title: IG MagnaGuard
 */
public class Card211_060 extends AbstractDroid {
    public Card211_060() {
        super(Side.DARK, 2, 3, 3, 4, "IG MagnaGuard", Uniqueness.RESTRICTED_3);
        setArmor(4);
        setLore("Assassin.  Trade Federation.");
        setGameText("Grievous is defense value +2 here.  While aboard Invisible Hand, draws one battle destiny if unable to otherwise and characters here are immune to Clash Of Sabers.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_I, Icon.PRESENCE, Icon.WARRIOR, Icon.SEPARATIST);
        addModelType(ModelType.BATTLE);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter GrievousAtSameLocation = Filters.and(Filters.Grievous, Filters.atSameLocation(self));
        Condition AboardInvisibleHandAsPassenger = new AboardCondition(self, Filters.Invisible_Hand);
        Condition AtInvisibleHandSite = new AtCondition(self, Filters.Invisible_Hand_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, GrievousAtSameLocation, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, AboardInvisibleHandAsPassenger, 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, AtInvisibleHandSite, 1));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.character, Filters.atSameLocation(self)),AtInvisibleHandSite, Title.Clash_Of_Sabers));

        return modifiers;
    }
}
