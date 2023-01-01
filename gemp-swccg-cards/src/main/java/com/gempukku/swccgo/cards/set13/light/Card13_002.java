package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: A Remote Planet
 */
public class Card13_002 extends AbstractNormalEffect {
    public Card13_002() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Remote Planet", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("In unfamiliar and potentially hostile territory, Qui-Gon knew that success would require patience and caution.");
        setGameText("Deploy on table. At [Episode I] Tatooine battleground sites, your Force drains are +1 and opponent's cards with ability are deploy -1. While Padme at a Tatooine battleground site, opponent's Force drains at related [Episode I] sites are -1. Place Effect in Used Pile if canceled.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter episode1TatooineBattlegroundSites = Filters.and(Icon.EPISODE_I, Filters.Tatooine_battleground_site);
        Filter opponentCardsWithAbility = Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, episode1TatooineBattlegroundSites, 1, playerId));
        modifiers.add(new DeployCostToLocationModifier(self, opponentCardsWithAbility, -1, episode1TatooineBattlegroundSites));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.relatedSiteTo(self, Filters.Padme)),
                new AtCondition(self, Filters.Padme, Filters.Tatooine_battleground_site), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self));
        return modifiers;
    }
}