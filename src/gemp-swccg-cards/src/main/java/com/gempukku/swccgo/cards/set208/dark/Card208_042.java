package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AllCharactersAtLocationsAreCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentWhereAffectedCardIsAtEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MinimumDefenseValueReducedToModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Effect
 * Title: Where Are Those Droidekas?! (V)
 */
public class Card208_042 extends AbstractNormalEffect {
    public Card208_042() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Where_Are_Those_Droidekas, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("When two Jedi are attempting to breach your bridge, even a destroyer droid's response time seems far too slow.");
        setGameText("Deploy on table. Non-unique destroyer droids are deploy -1 and forfeit +1. While all your characters at sites are destroyer droids and Neimoidians, opponent's characters are defense value -1 (to a minimum of 3) for each destroyer droid present. [Immune to Alter]");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_SET_8);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter nonUniqueDestroyerDroids = Filters.and(Filters.non_unique, Filters.destroyer_droid);
        Condition condition =
                new OrCondition(
                        new AllCharactersAtLocationsAreCondition(self, playerId, Filters.site, Filters.or(Filters.destroyer_droid, Filters.Neimoidian)),
                        new AndCondition(new GameTextModificationCondition(self, ModifyGameTextType.WAT_TAMBOR__IGNORED_BY_WHERE_ARE_THOSE_DROIDEKAS),
                                new AllCharactersAtLocationsAreCondition(self, playerId, Filters.site, Filters.or(Filters.destroyer_droid, Filters.Neimoidian, Filters.title("Wat Tambor")))
                        ));

        Filter opponentsCharactersDV3Plus = Filters.and(Filters.opponents(self), Filters.character, Filters.defenseValueMoreThanOrEqualTo(3), Filters.at(Filters.wherePresent(self, Filters.destroyer_droid)));
        Filter opponentsCharactersAll = Filters.and(Filters.opponents(self), Filters.character, Filters.at(Filters.wherePresent(self, Filters.destroyer_droid)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, nonUniqueDestroyerDroids, -1));
        modifiers.add(new ForfeitModifier(self, nonUniqueDestroyerDroids, 1));
        modifiers.add(new DefenseValueModifier(self, opponentsCharactersDV3Plus, condition, new NegativeEvaluator(new PresentWhereAffectedCardIsAtEvaluator(self, Filters.destroyer_droid))));
        modifiers.add(new MinimumDefenseValueReducedToModifier(self, opponentsCharactersAll, condition, 3));
        return modifiers;
    }
}