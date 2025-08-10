package com.gempukku.swccgo.cards.set2.light;

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
 * Title: Scanner Techs
 */
public class Card2_037 extends AbstractNormalEffect {
    public Card2_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Scanner_Techs, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Specialized scanner technicians examine scanner output to identify the presence of life forms. Experienced operators can even identify species and gender.");
        setGameText("Deploy on your side of table. Radar Scanner is immune to Sense. If Radar Scanner is played against an opponent who has more than 13 cards in hand, opponent loses all Jawas, Tusken Raiders and stormtroopers found there.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Radar_Scanner, Title.Sense));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Radar_Scanner),
                new CardsInHandEqualToOrMoreThanCondition(opponent, 13), ModifyGameTextType.RADAR_SCANNER__JAWAS_TUSKEN_RAIDERS_AND_STORMTROOPERS_LOST));
        return modifiers;
    }
}