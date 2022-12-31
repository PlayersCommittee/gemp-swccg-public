package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrMoreThanCondition;
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
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Hyperwave Scan
 */
public class Card2_120 extends AbstractNormalEffect {
    public Card2_120() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Hyperwave Scan", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Full Imperial scans include full-spectrum transceivers, dedicated energy receptors, crystal gravfield traps, and hyperwave signal interceptors.");
        setGameText("Deploy on your side of table. Scanning Crew is immune to Sense. If Scanning Crew is played against an opponent who has 13 or more cards in hand, opponent also loses all cards with 'Rebel' in the title found there.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Scanning_Crew, Title.Sense));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Scanning_Crew),
                new CardsInHandEqualToOrMoreThanCondition(opponent, 13), ModifyGameTextType.SCANNING_CREW__CARDS_WITH_REBEL_IN_TITLE_LOST));
        return modifiers;
    }
}