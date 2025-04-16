package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Capital
 * Title: Ferfiek Chawa
 */
public class Card304_067 extends AbstractCapitalStarship {
    public Card304_067() {
        super(Side.LIGHT, 1, 3, 1, 3, null, 2, 5, "Ferfiek Chawa", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Used by Claudius the Hutt as his mobile pleasure yacht. Claudius spent a small fortune on improving the interior of the yacht while leaving it's exterior a 'stock'.");
        setGameText("May add 1 pilot and 6 passengers. Deploys and moves like a starfighter. Has ship-docking capability. During battle, your total battle destiny is +1 for each Clan Tiure aboard. Permanent pilot aboard provides ability of 1.");
        addIcons(Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MINSTREL);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        addPersona(Persona.FERFIEK_CHAWA);
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
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
	
	@Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleCondition(self),
                new OnTableEvaluator(self, Filters.and(Filters.Clan_Tiure, Filters.aboard(self))), playerId));
        return modifiers;
    }
}
