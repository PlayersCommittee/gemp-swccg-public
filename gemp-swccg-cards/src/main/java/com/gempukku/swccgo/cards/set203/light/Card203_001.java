package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Admiral Ackbar (V)
 */
public class Card203_001 extends AbstractRebel {
    public Card203_001() {
        super(Side.LIGHT, 1, 4, 3, 3, 7, Title.Ackbar, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Mon Calamari leader of Rebel fleet. Master military strategist. Early prisoner of Grand Moff Tarkin. Convinced his people to join the Alliance.");
        setGameText("[Pilot] 3: any capital starship. Deploys free aboard Home One. While piloting a Star Cruiser, adds one battle destiny. Where you have two Rebel capital starships, attrition against opponent is +2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setSpecies(Species.MON_CALAMARI);
        setMatchingStarshipFilter(Filters.Home_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.capital_starship));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, Filters.Star_Cruiser), 1));
        modifiers.add(new AttritionModifier(self, new DuringBattleWithParticipantCondition(2, Filters.and(Filters.your(self),
                Filters.Rebel_capital_starship)), 2, opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Persona.HOME_ONE));
        return modifiers;
    }
}
