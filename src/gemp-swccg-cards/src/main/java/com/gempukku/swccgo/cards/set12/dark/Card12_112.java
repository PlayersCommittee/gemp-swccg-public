package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AboardStarshipOrVehicleOfPersonaCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Nute Gunray
 */
public class Card12_112 extends AbstractRepublic {
    public Card12_112() {
        super(Side.DARK, 3, 3, 3, 4, 6, Title.Nute_Gunray, Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Commanding Viceroy of the Trade Federation forces assigned to the blockade of Naboo. Takes for his own actions. Neimoidian leader.");
        setGameText("Your destroyer droids and battle droids are each forfeit +1 at same and related sites. While aboard Blockade Flagship, opponent's Jedi may not move or deploy to Bridge, and your destroyer droids are destiny +3 if drawn for battle destiny.");
        addPersona(Persona.GUNRAY);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition aboardBlockadeFlagship = new AboardStarshipOrVehicleOfPersonaCondition(self, Persona.BLOCKADE_FLAGSHIP);
        Filter opponentsJedi = Filters.and(Filters.opponents(self), Filters.Jedi);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.destroyer_droid, Filters.battle_droid),
                Filters.atSameOrRelatedSite(self)), 1));
        modifiers.add(new MayNotMoveToLocationModifier(self, opponentsJedi, aboardBlockadeFlagship, Filters.BlockadeFlagshipBridge));
        modifiers.add(new MayNotDeployToLocationModifier(self, opponentsJedi, aboardBlockadeFlagship, Filters.BlockadeFlagshipBridge));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, Filters.and(Filters.your(self), Filters.destroyer_droid),
                aboardBlockadeFlagship, 3));
        return modifiers;
    }
}
