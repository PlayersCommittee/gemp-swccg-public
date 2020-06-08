package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
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
        super(Side.DARK, 2, 3, 4, 1, Title.IT0, Uniqueness.UNIQUE);
        setLore("Floating prisoner interrogation droid. Uses probes and needles to dispense truth drugs and perform 'surgery.' Sensors determine subject's pain threshold and truthfulness.");
        setGameText("When at Detention Block Corridor, adds X to your Force drains there, where X = the number of captives present. Immune to Restraining Bolt.");
        addModelType(ModelType.INTERROGATOR);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AndCondition(new AtCondition(self, Filters.Detention_Block_Corridor),
                new PhaseCondition(Phase.CONTROL)), new PresentEvaluator(self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive), self.getOwner()));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        return modifiers;
    }
}
