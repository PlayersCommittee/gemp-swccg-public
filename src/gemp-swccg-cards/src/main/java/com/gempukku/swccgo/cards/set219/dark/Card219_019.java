package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: The Emperor, Relentless
 */
public class Card219_019 extends AbstractDarkJediMasterImperial {
    public Card219_019() {
        super(Side.DARK, 6, 5, 4, 7, 9, "The Emperor, Relentless", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("Leader. Secretive manipulator of the galaxy. Played Darth Vader and Prince Xizor off against one another in his relentless pursuit of 'young Skywalker.'");
        setGameText("Never deploys or moves (even if carried) to a site opponent occupies. Players do not retrieve Force for initiating a battle. If Agents Of The Black Sun on table, your total battle destiny where you have a Black Sun agent is +1. Immune to attrition.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.REFLECTIONS_II, Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;

    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition agentsOfBlackSunOnTable = new OnTableCondition(self, Filters.Agents_Of_Black_Sun);
        
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_RETRIEVE_FORCE_FOR_INITIATING_BATTLE, playerId));
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_RETRIEVE_FORCE_FOR_INITIATING_BATTLE, opponent));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.your(playerId), Filters.Black_Sun_agent)), agentsOfBlackSunOnTable, 1, playerId, true));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
