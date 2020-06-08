package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.UndercoverCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Rebel
 * Title: Boushh
 */
public class Card110_001 extends AbstractRebel {
    public Card110_001() {
        super(Side.LIGHT, 1, 6, 3, 4, 7, "Boushh", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Leia obtained the armor of a notorious mercenary to sneak onto Coruscant. She later assumed the same role to spy on Jabba. Fearless and inventive. Jabba's kind of scum.");
        setGameText("Deploys only to a site (-2 on Tatooine or Coruscant) as an Undercover Spy. While undercover at an opponents site, prevents opponent from modifying or cancelling your Force Drains at opponent's related sites. Immune to Attrition < 3.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.FEMALE);
        addPersona(Persona.LEIA);
        setSpecies(Species.ALDERAANIAN);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Tatooine_site, Filters.Coruscant_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition undercoverAtOpponentsSite = new AndCondition(new UndercoverCondition(self), new AtCondition(self, Filters.and(Filters.opponents(self), Filters.site)));
        Filter opponentsRelatedSites = Filters.and(Filters.opponents(self), Filters.relatedSite(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, opponentsRelatedSites, undercoverAtOpponentsSite, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, opponentsRelatedSites, undercoverAtOpponentsSite, opponent, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
