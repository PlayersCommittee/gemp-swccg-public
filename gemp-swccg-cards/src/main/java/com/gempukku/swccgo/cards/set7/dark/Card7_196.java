package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.evaluators.PerTIEPresentInBattleEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Droid
 * Title: R2-A5 (Artoo-Ayfive)
 */
public class Card7_196 extends AbstractDroid {
    public Card7_196() {
        super(Side.DARK, 2, 2, 1, 3, "R2-A5 (Artoo-Ayfive)", Uniqueness.UNIQUE);
        setLore("Combat communications astromech. Coordinates starfighter attack vectors. Broadcasts target acquisition data to gunnery systems.");
        setGameText("When in battle aboard your capital starship at a system or sector, adds 1 to total attrition against opponent for each of your piloted TIEs present in that battle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, new AndCondition(new InBattleAtCondition(self, Filters.system_or_sector),
                new AboardCondition(self, Filters.and(Filters.your(self), Filters.capital_starship))),
                new PerTIEPresentInBattleEvaluator(self, Filters.and(Filters.your(self), Filters.piloted, Filters.TIE)),
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
