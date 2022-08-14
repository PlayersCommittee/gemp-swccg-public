package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Droid
 * Title: R3-A2 (Arthree-Aytoo) (V)
 */
public class Card217_045 extends AbstractDroid {
    public Card217_045() {
        super(Side.LIGHT, 2, 2, 1, 3, "R3-A2 (Arthree-Aytoo)", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Special-purpose astromech capable of coordinating piloting coordinates and approach angles during combat.");
        setGameText("Your starships here with an astromech character aboard are power +1 (or +2 if also at Hoth), immune to Lateral Damage, and may move to systems or sectors as a 'react.'");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_17);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.starship, Filters.here(self),
                Filters.hasAboard(self, Filters.and(Filters.astromech_droid, Filters.character)));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, filter, new ConditionEvaluator(1, 2, new AtCondition(self, Filters.Hoth_system))));
        modifiers.add(new ImmuneToTitleModifier(self, filter, Title.Lateral_Damage));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move a starship as a react", self.getOwner(), filter, Filters.or(Filters.system, Filters.sector)));
        return modifiers;
    }
}
