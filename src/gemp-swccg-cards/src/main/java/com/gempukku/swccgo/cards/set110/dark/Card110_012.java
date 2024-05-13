package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.TotalAbilityPilotingMoreThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Zuckuss In Mist Hunter
 */
public class Card110_012 extends AbstractStarfighter {
    public Card110_012() {
        super(Side.DARK, 1, 6, 2, null, 3, 5, 5, "Zuckuss In Mist Hunter", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_JABBAS_PALACE, Rarity.PM);
        setLore("Zuckuss is a dangerous adversary, especially when aboard his own starship. Mystical omens enable the Gand to predict enemy maneuvers in starship combat.");
        setGameText("May add 1 pilot and 3 passengers. Permanent pilot is •Zuckuss, who provides ability of 4 and adds 2 to power. Unless opponent has total ability > 6 piloting here, opponent's total battle destiny here = zero.");
        addPersonas(Persona.MIST_HUNTER);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.BYBLOS_G1A_TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.ZUCKUSS, 4) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        return modifiers;
                    }
                });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetTotalBattleDestinyModifier(self, Filters.here(self), new AndCondition(new InBattleCondition(self),
                new UnlessCondition(new TotalAbilityPilotingMoreThanCondition(opponent, 6, Filters.here(self)))), 0, opponent));
        return modifiers;
    }
}
