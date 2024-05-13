package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Goo Nee Tay
 */
public class Card7_062 extends AbstractNormalEffect {
    public Card7_062() {
        super(Side.LIGHT, 4, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Goo_Nee_Tay, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("'Nah nah nah. Ohhhh! Louwa! GOO NEE TAY!'");
        setGameText("Deploy on opponent's side of table. All opponent's characters of ability > 2 are deploy +2. Also, opponent may not play Surprise and may not use M'iiyoom Onith's game text.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(2)), 2));
        modifiers.add(new MayNotPlayModifier(self, Filters.Surprise, opponent));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Miiyoom_Onith)));
        return modifiers;
    }
}