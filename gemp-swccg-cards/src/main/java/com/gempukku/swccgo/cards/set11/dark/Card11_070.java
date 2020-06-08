package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: The Camp
 */
public class Card11_070 extends AbstractNormalEffect {
    public Card11_070() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.His_Name_Is_Anakin, Uniqueness.UNIQUE);
        setLore("Anakin dreamt that one day he would return to Tatooine to free the slaves. His aggression will one day be felt by the entire Republic.");
        setGameText("Deploy on table. Each non-battleground docking bay generates no more than one Force for either player. Opponent's aliens deploy +1 on Tatooine (or +2 if non-unique or to same Tatooine site as your Dark Jedi, or +4 if both). (Immune to Alter.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter nonBattlegroundDockingBay = Filters.and(Filters.docking_bay, Filters.non_battleground_location);
        Filter opponentsAlien = Filters.and(Filters.opponents(self), Filters.alien);
        Filter onTatooine = Filters.Deploys_on_Tatooine;
        Filter sameSiteAsYourDarkJedi = Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Dark_Jedi));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceGenerationModifier(self, nonBattlegroundDockingBay, 1, playerId));
        modifiers.add(new LimitForceGenerationModifier(self, nonBattlegroundDockingBay, 1, opponent));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.unique, opponentsAlien), 1, Filters.and(onTatooine, Filters.not(sameSiteAsYourDarkJedi)), true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.non_unique, opponentsAlien), 2, Filters.and(onTatooine, Filters.not(sameSiteAsYourDarkJedi)), true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.unique, opponentsAlien), 2, Filters.and(onTatooine, sameSiteAsYourDarkJedi), true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.non_unique, opponentsAlien), 4, Filters.and(onTatooine, sameSiteAsYourDarkJedi), true));
        return modifiers;
    }
}