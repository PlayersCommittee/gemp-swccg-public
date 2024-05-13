package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AloneAtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Dia Gida
 */
public class Card304_054 extends AbstractAlien {
    public Card304_054() {
        super(Side.LIGHT, 2, 4, 4, 3, 5, "Dia Gida", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Dia made her name as a crack shot, performing trick shots other gunslingers couldn't pull off. Currently working as a sniper for the Tiure Hutt clan.");
        setGameText("When alone at a site, draws one battle destiny if not able to otherwise. Adds 2 to each of her weapon destiny draws. Immune to attrition < X, where X=number of [CSP Icon] present.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.CLAN_TIURE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AloneAtCondition(self, Filters.site), 2));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new PresentEvaluator(self, Filters.CSP)));
        return modifiers;
    }
}
