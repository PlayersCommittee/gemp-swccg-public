package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.TotalAbilityPilotingMoreThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetTotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: First Officer Thaneespi
 */
public class Card9_012 extends AbstractRebel {
    public Card9_012() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, "First Officer Thaneespi", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Mon Calamari deck officer. Brilliant tactical analyst. One of several brave leaders of Mon Calamari refugees from Imperial invasion of her planet.");
        setGameText("Deploys -2 to Home One. Adds 3 to power of any capital starship she pilots. When piloting a Star Cruiser with another Mon Calamari pilot aboard, unless opponent has total ability > 6 piloting here, opponent's total battle destiny here = zero.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.FEMALE);
        setSpecies(Species.MON_CALAMARI);
        setMatchingStarshipFilter(Filters.Home_One);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -2, Persona.HOME_ONE));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.capital_starship));
        modifiers.add(new ResetTotalBattleDestinyModifier(self, Filters.here(self),
                new AndCondition(new PilotingCondition(self, Filters.and(Filters.Star_Cruiser,
                        Filters.hasAboard(self, Filters.and(Filters.other(self), Filters.Mon_Calamari_character, Filters.pilot)))),
                        new UnlessCondition(new TotalAbilityPilotingMoreThanCondition(opponent, 6, Filters.here(self)))),
                0, opponent));
        return modifiers;
    }
}
