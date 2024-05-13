package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: K-3PO (Kay-Threepio)
 */
public class Card3_012 extends AbstractDroid {
    public Card3_012() {
        super(Side.LIGHT, 4, 2, 1, 3, "K-3PO (Kay-Threepio)", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R1);
        setLore("Old-model protocol droid. Learned military tactics while under the ownership of Commander Narra. In charge of the droid pool at Echo Base.");
        setGameText("May initiate battle and be battled. K-3PO is power +1 for each of your other droids at same Hoth or Yavin 4 site. Functions as a leader if present with another of your droids.");
        addIcons(Icon.HOTH);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourOtherDroids = Filters.and(Filters.your(self), Filters.other(self), Filters.droid);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Hoth_site, Filters.Yavin_4_site)),
                new AtSameSiteEvaluator(self, yourOtherDroids)));
        modifiers.add(new LeaderModifier(self, new PresentWithCondition(self, yourOtherDroids)));
        return modifiers;
    }
}
