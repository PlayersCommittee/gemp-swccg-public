package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.DuringPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.evaluators.NumCopiesOfCardAtLocationEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Defensive Shield
 * Title: Yavin Sentry & Goldenrod
 */
public class Card223_049 extends AbstractDefensiveShield {
    public Card223_049() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Yavin Sentry & Goldenrod", ExpansionSet.SET_23, Rarity.V);
        addComboCardTitles(Title.Yavin_Sentry, Title.Goldenrod);
        setGameText("Plays on table. They Must Never Again Leave This City is suspended during opponent's first turn. For opponent to deploy a character, starship, or vehicle for free (except by that card's own game text), opponent must first use 2 Force. Opponent must first use X Force to deploy a non-unique card (except a Jawa or Tusken Raider) to a location, where X = the number of copies of that card at that location.");
        addIcon(Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }
    
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), 2));
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.They_Must_Never_Again_Leave_This_City), new DuringPlayersTurnNumberCondition(game.getOpponent(self.getOwner()), 1)));
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.non_unique, Filters.except(Filters.or(Filters.Jawa, Filters.Tusken_Raider))),
                new NumCopiesOfCardAtLocationEvaluator(self)));
        return modifiers;
    }

}
