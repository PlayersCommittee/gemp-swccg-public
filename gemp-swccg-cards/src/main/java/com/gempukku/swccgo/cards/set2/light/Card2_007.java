package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.IncreaseAbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Doikk Na'ts
 */
public class Card2_007 extends AbstractAlien {
    public Card2_007() {
        super(Side.LIGHT, 3, 2, 1, 1, 3, "Doikk Na'ts", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.U2);
        setLore("Male Bith musician. Plays Fizz (Dorenian Beshniquel) in Figrin D'an's band. Frustrated by sentients, especially humans, he prefers to work with droids.");
        setGameText("For each other musician at same site, the ability required to draw battle destiny here increases by 1 for both players.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.BITH);
        addPersona(Persona.DOIKK_NATS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSite = Filters.sameSite(self);
        Evaluator forEachMusicianAtSameSite = new AtSameSiteEvaluator(self, Filters.and(Filters.other(self), Filters.musician));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IncreaseAbilityRequiredForBattleDestinyModifier(self, sameSite, forEachMusicianAtSameSite, playerId));
        modifiers.add(new IncreaseAbilityRequiredForBattleDestinyModifier(self, sameSite, forEachMusicianAtSameSite, game.getOpponent(playerId)));
        return modifiers;
    }
}
