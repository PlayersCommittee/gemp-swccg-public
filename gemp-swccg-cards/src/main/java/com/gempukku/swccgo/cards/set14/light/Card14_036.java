package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Steady, Steady
 */
public class Card14_036 extends AbstractNormalEffect {
    public Card14_036() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Steady_Steady, Uniqueness.UNIQUE);
        setLore("The Gungan army hoped that by initiating a conflict at the battle plains, they could draw the Trade Federation's forces away from Amidala's real plan.");
        setGameText("Deploy on table. If you have two Gungans with different card titles at Battle Plains, your Force drains are +2 there. While two underwater sites on table, your Gungans and creature vehicles are immune to attrition < 4 where your Fambaa is present. (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.Battle_Plains,
                Filters.hasDifferentCardTitlesAtLocation(self, Filters.and(Filters.your(self), Filters.Gungan))), 2, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Gungan, Filters.creature_vehicle),
                Filters.at(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Fambaa)))),
                new OnTableCondition(self, 2, Filters.underwater_site), 4));
        return modifiers;
    }
}