package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Alien
 * Title: Kazak
 */
public class Card8_017 extends AbstractAlien {
    public Card8_017() {
        super(Side.LIGHT, 3, 2, 0, 2, 1, "Kazak", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Elder Ewok advisor to Chief Chirpa. Studied Imperial patrols and helped plan attacks.");
        setGameText("Deploys only on Endor. Power and forfeit +1 for each Light side icon at same Endor site. When present at an Endor site, adds 1 to your Force drains at each Endor site where another of your Ewoks is present.");
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
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.Endor_site, Filters.wherePresent(self,
                Filters.and(Filters.other(self), Filters.your(self), Filters.Ewok))),
                new PresentAtCondition(self, Filters.Endor_site), 1, self.getOwner()));
        return modifiers;
    }
}
