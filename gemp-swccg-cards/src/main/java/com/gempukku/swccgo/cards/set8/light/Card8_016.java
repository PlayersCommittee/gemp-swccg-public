package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Alien
 * Title: Graak
 */
public class Card8_016 extends AbstractAlien {
    public Card8_016() {
        super(Side.LIGHT, 3, 2, 0, 1, 1, Title.Graak, Uniqueness.UNIQUE);
        setLore("Cunning Ewok. Led successful attacks, encircling enemies and cutting off their retreats.");
        setGameText("Deploys only on Endor. Power and forfeit +1 for each Light side icon at same Endor site. When opponent is losing Force from Force drains at same or adjacent Endor site, lost Force must come from Reserve Deck if possible.");
        addIcons(Icon.ENDOR);
        setSpecies(Species.EWOK);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Endor;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atEndorSite = new AtCondition(self, Filters.Endor_site);
        Evaluator lightSideIconsAtLocation = new ForceIconsAtLocationEvaluator(self, false, true);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atEndorSite, lightSideIconsAtLocation));
        modifiers.add(new ForfeitModifier(self, atEndorSite, lightSideIconsAtLocation));
        modifiers.add(new SpecialFlagModifier(self, new DuringForceDrainAtCondition(Filters.and(Filters.Endor_site, Filters.sameOrAdjacentSite(self))),
                ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
