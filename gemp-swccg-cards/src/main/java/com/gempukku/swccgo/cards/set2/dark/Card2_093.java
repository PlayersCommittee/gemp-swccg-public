package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: IT-O (Eyetee-Oh)
 */
public class Card2_093 extends AbstractDroid {
    public Card2_093() {
        super(Side.DARK, 2, 3, 4, 1, Title.IT0, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("Floating prisoner interrogation droid. Uses probes and needles to dispense truth drugs and perform 'surgery.' Sensors determine subject's pain threshold and truthfulness.");
        setGameText("When at Detention Block Corridor, adds X to your Force drains here, where X = number of captives here. Immune to Restraining Bolt.");
        addModelType(ModelType.INTERROGATOR);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new AtCondition(self, Filters.Detention_Block_Corridor), new HereCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive)), new HereEvaluator(self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive), self.getOwner()));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        return modifiers;
    }
}
