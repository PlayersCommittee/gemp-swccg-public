package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: SSA-1015
 */
public class Card14_089 extends AbstractDroid {
    public Card14_089() {
        super(Side.DARK, 2, 3, 2, 3, "SSA-1015", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Security battle droid whose programming assured him a Jedi could be subdued. Supposed to be designated SSA-101, but a mistake in production pushed him back to 1015.");
        setGameText("While with an opponent's Jedi, your other battle droids present are power +1. Opponent's Jedi are deploy +4 to same site. While with another battle droid at a site, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.SECURITY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.battle_droid, Filters.present(self)),
                new WithCondition(self, Filters.and(Filters.opponents(self), Filters.Jedi)), 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.Jedi), 4, Filters.sameSite(self)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new WithCondition(self, Filters.battle_droid),
                new AtCondition(self, Filters.site)), 1));
        return modifiers;
    }
}
