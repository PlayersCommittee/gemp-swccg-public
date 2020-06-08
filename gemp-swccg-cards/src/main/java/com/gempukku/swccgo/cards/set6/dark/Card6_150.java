package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
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
 * Title: Well Guarded
 */
public class Card6_150 extends AbstractNormalEffect {
    public Card6_150() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Well_Guarded, Uniqueness.UNIQUE);
        setLore("Most of Jabba's guards had been sold to the Hutt and were too scared (or too dumb) to leave. Jabba assigned his best guards to watch over his most prized possessions.");
        setGameText("Deploy on your side of table. While you have fewer than 13 cards in your hand, your non-unique cards in hand (except effects of any kind and any interrupts) are immune to Grimtaash. (Immune to Alter while you occupy 2 battlegrounds).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.inHand(playerId), Filters.non_unique, Filters.not(Filters.Effect_of_any_Kind),
                Filters.not(Filters.Interrupt)), new CardsInHandFewerThanCondition(playerId, 13), Title.Grimtaash));
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