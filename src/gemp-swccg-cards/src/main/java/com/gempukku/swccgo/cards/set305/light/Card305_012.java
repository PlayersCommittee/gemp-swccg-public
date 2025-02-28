package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Starship
 * Subtype: Capital
 * Title: Vesper II
 */
public class Card305_012 extends AbstractCapitalStarship {
    public Card305_012() {
        super(Side.LIGHT, 1, 9, 9, 8, null, 4, 10, "Vesper II", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.UR);
        setLore("Following the attack by Starkiller Base, numerous New Republic vessels found their way into the hands of friendly worlds. It is unknown where Clan Odan-Urr acquired the Vesper II.");
        setGameText("May add 5 pilots, 6 passengers, 1 vehicle and 3 starfighters. Has ship-docking capability. Permanent pilot aboard provides ability of 2. Adds 1 to attrition against opponent here for each piloted [COU] starship here. Immune to attrition < 12");
        addIcons(Icon.COU, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.DEFENDER_CLASS_CRUISER);
        setPilotCapacity(5);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setStarfighterCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AttritionModifier(self, Filters.here(self), new HereEvaluator(self, Filters.and(Filters.piloted, Filters.COU_starship)), opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 12));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
