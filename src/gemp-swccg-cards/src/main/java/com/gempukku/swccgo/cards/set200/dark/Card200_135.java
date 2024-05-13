package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployInsteadOfStarfighterUsingCombatResponseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Capital
 * Title: Hound's Tooth (V)
 */
public class Card200_135 extends AbstractCapitalStarship {
    public Card200_135() {
        super(Side.DARK, 2, 3, 5, 4, null, 4, 6, "Hound's Tooth", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Controlled by state-of-the-art-voice-activated X10-D computers. Internal sensors and security systems monitor prisoner activity. Modified for Bossk's Trandoshan physiology.");
        setGameText("May add 1 pilot (must be smuggler or bounty hunter), 6 passengers and 1 vehicle. Immune to attrition < 4 if Bossk piloting. Deploys and moves like a starfighter. Has ship-docking capability.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        addModelType(ModelType.MODIFIED_CORELLIAN_FREIGHTER);
        addPersona(Persona.HOUNDS_TOOTH);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setMatchingPilotFilter(Filters.Bossk);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    @Override
    public boolean isDeploysLikeStarfighter() {
        return true;
    }


    @Override
    public boolean isMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployInsteadOfStarfighterUsingCombatResponseModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition bosskPiloting = new HasPilotingCondition(self, Filters.Bossk);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, bosskPiloting, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, bosskPiloting, 4));
        return modifiers;
    }
}
