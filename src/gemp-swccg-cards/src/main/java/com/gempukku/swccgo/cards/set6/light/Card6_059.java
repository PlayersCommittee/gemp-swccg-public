package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Yarna d'al' Gargan
 */
public class Card6_059 extends AbstractNormalEffect {
    public Card6_059() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Yarna_dal_Gargan, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Female dancer from Askajia. Very protective mother. Makes sure that all those she cares about are protected.");
        setGameText("Deploy on your side of table. While you have fewer than 13 cards in hand, your non-unique cards in hand (except Effects of any kind and Interrupts) are immune to Monnok. (Immune to Alter while you occupy two battlegrounds).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.inHand(playerId), Filters.non_unique, Filters.not(Filters.Effect_of_any_Kind),
                Filters.not(Filters.Interrupt)), new CardsInHandFewerThanCondition(playerId, 13), Title.Monnok));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OccupiesCondition(playerId, 2, Filters.battleground), Title.Alter));
        return modifiers;
    }
}