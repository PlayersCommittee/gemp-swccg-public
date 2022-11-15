package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainBonusesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Menace Fades
 */
public class Card9_038 extends AbstractNormalEffect {
    public Card9_038() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Menace_Fades, Uniqueness.UNIQUE);
        setLore("As the Executor crashed into the Death Star, hope surged throughout the ranks of the outmanned Rebel fleet.");
        setGameText("Deploy on table. While you control any three Coruscant, Death Star and/or Death Star II locations, or any battleground site and one battleground system, all opponents Force drain bonuses everywhere are canceled. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self,
                        new OrCondition(new ControlsCondition(playerId, 3, Filters.or(Filters.Coruscant_location, Filters.Death_Star_location, Filters.Death_Star_II_location)),
                                        new AndCondition(new ControlsCondition(playerId, Filters.battleground_site), new ControlsCondition(playerId, Filters.battleground_system)))));
        return modifiers;
    }
}