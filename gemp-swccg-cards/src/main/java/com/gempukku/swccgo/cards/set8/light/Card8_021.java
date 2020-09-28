package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Alien
 * Title: Lumat
 */
public class Card8_021 extends AbstractAlien {
    public Card8_021() {
        super(Side.LIGHT, 3, 1, 0, 1, 1, "Lumat", Uniqueness.UNIQUE);
        setLore("Ewok archer. Wood carver. Tribal elder that advises Chief Chirpa. Crafts and maintains weapons.");
        setGameText("Deploys only on Endor. Power and forfeit +1 for each Light side icon at same Endor site. When on Endor, adds 1 to total weapon destiny whenever you have any Ewok using an Ewok weapon at an Endor site.");
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
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.and(Filters.Ewok_weapon, Filters.at(Filters.Endor_site)),
                new OnCondition(self, Title.Endor), Filters.Ewok, 1));
        return modifiers;
    }
}
