package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Starship
 * Subtype: Capital
 * Title: Lightmaker
 */
public class Card209_030 extends AbstractCapitalStarship {
    public Card209_030() {
        super(Side.LIGHT, 2, 5, 4, 5, null, 3, 5, Title.Lightmaker, Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLore("Phoenix Squadron.");
        setGameText("Permanent pilot provides ability of 2. When in battle, adds 1 to attrition against opponent for each of their starships here. Cancels opponent's immunity to attrition here.");
        addIcons(Icon.VIRTUAL_SET_9, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.PHOENIX_SQUADRON);
        addModelType(ModelType.HAMMERHEAD_CORVETTE);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, new InBattleCondition(self), new PresentEvaluator(self,
                Filters.and(Filters.opponents(self), Filters.starship)), opponent));
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.and(Filters.opponents(self), Filters.atSameLocation(self))));
        return modifiers;
    }
}
