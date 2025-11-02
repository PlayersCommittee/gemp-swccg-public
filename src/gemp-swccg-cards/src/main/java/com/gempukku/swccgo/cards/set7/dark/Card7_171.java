package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.IsOnlyExcludedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DoubledModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.NotUniqueModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Grenwick
 */
public class Card7_171 extends AbstractImperial {
    public Card7_171() {
        super(Side.DARK, 3, 1, 2, 1, 2, "Corporal Grenwick", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Death Star trooper who witnessed Leia's interrogation. Senior tactical advisor to Sergeant Major Enfield. Coordinates security duty assignments for Detention Block AA-23.");
        setGameText("While Grenwick is on Death Star, your Death Star Sentry is not unique (•), is doubled, deploys free, applies all three of its modifiers and is immune to Alter. Power -1 when not on Death Star.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.DEATH_STAR_TROOPER);
        addPersona(Persona.GRENWICK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onDeathStarCondition = new OnCondition(self, Filters.Grenwick, Title.Death_Star);
        Filter deathStarSentryFilter = Filters.and(Filters.your(self), Filters.Death_Star_Sentry);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, deathStarSentryFilter, onDeathStarCondition));
        modifiers.add(new DoubledModifier(self, deathStarSentryFilter, onDeathStarCondition));
        modifiers.add(new DeploysFreeModifier(self, deathStarSentryFilter, onDeathStarCondition));
        modifiers.add(new ModifyGameTextModifier(self, deathStarSentryFilter, onDeathStarCondition, ModifyGameTextType.DEATH_STAR_SENTRY__APPLIES_ALL_MODIFIERS));
        modifiers.add(new ImmuneToTitleModifier(self, deathStarSentryFilter, onDeathStarCondition, Title.Alter));
        modifiers.add(new PowerModifier(self, new NotCondition(onDeathStarCondition), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        //Excluded From Battle - special rules exception:
        //"being excluded will not cause ... other cards to be canceled or otherwise removed from table"
        Condition onDeathStarCondition = new OnCondition(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Grenwick, Title.Death_Star);
        Filter deathStarSentryFilter = Filters.and(Filters.your(self), Filters.Death_Star_Sentry);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        //prevent Death Star Sentry from being removed from table
        modifiers.add(new NotUniqueModifier(self, deathStarSentryFilter, new AndCondition(onDeathStarCondition, new IsOnlyExcludedCondition(self))));
        return modifiers;
    }
}
