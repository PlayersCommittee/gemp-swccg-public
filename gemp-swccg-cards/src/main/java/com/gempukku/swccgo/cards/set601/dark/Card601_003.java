package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Bossk (V)
 */
public class Card601_003 extends AbstractAlien {
    public Card601_003() {
        super(Side.DARK, 1, 3, 4, 2, 5, "Bossk", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Trandoshan bounty hunter. Strong but clumsy. Extremely proud and arrogant. Suffered a humiliating defeat at the hands of Chewbacca and his partner Han Solo.");
        setGameText("[Pilot] 2. While at same site as a smuggler, power +3. While with opponent's smuggler or Wookiee, opponent may not draw more than one battle destiny here. Immune to attrition < number of opponent's smugglers on table.");
        addPersona(Persona.BOSSK);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_2);
        setSpecies(Species.TRANDOSHAN);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new AtSameSiteAsCondition(self, Filters.smuggler), 3));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new WithCondition(self,
                Filters.and(Filters.opponents(self), Filters.or(Filters.smuggler, Filters.Wookiee))), 1, opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnTableEvaluator(self, Filters.and(Filters.opponents(self), Filters.smuggler))));
        return modifiers;
    }
}
