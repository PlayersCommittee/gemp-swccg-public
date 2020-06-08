package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Tusken Raider
 */
public class Card1_196 extends AbstractAlien {
    public Card1_196() {
        super(Side.DARK, 2, 2, 1, 1, 1, "Tusken Raider");
        setLore("'Sand People.' Ride banthas. Wield gaderffi (gaffi) sticks. Wear eye protectors and breath masks. Violent, nomadic, desert survival experts. 'Urrrg! Ur Ur Uur!'");
        setGameText("Deploys only on Tatooine. Power +1 if another non-unique Tusken Raider present. If you have four or more non-unique Tusken Raiders present, your total power here is +2.");
        setSpecies(Species.TUSKEN_RAIDER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentCondition(self, Filters.and(Filters.other(self), Filters.non_unique, Filters.Tusken_Raider)), 1));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new PresentCondition(self, 4, Filters.and(Filters.your(self), Filters.non_unique, Filters.Tusken_Raider)),
                2, self.getOwner()));
        return modifiers;
    }
}
