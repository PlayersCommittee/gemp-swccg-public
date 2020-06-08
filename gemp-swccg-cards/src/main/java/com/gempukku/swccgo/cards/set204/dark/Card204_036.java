package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Character
 * Subtype: Droid
 * Title: B2 Battle Droid
 */
public class Card204_036 extends AbstractDroid {
    public Card204_036() {
        super(Side.DARK, 2, 4, 4, 4, "B2 Battle Droid", Uniqueness.RESTRICTED_3);
        setArmor(5);
        setGameText("Draws one battle destiny if unable to otherwise. Attrition against opponent is +1 here.");
        addIcons(Icon.EPISODE_I, Icon.PRESENCE, Icon.SEPARATIST, Icon.VIRTUAL_SET_4);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
