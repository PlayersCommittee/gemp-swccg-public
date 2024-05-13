package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: SSA-306
 */
public class Card14_090 extends AbstractDroid {
    public Card14_090() {
        super(Side.DARK, 2, 3, 2, 3, "SSA-306", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setArmor(4);
        setLore("Security battle droid was assigned guard duty at Theed Palace. His shift can only be ended by a blaster shot.");
        setGameText("At same site, opponent's spies and smugglers are deploy +4 and may not apply their ability toward drawing battle destiny. While with another battle droid at a site, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.SECURITY_BATTLE_DROID, Keyword.GUARD);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentSpiesAndSmugglers = Filters.and(Filters.opponents(self), Filters.or(Filters.spy, Filters.smuggler));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, opponentSpiesAndSmugglers, 4, Filters.sameSite(self)));
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.and(opponentSpiesAndSmugglers, Filters.atSameSite(self))));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new WithCondition(self, Filters.battle_droid),
                new AtCondition(self, Filters.site)), 1));
        return modifiers;
    }
}
