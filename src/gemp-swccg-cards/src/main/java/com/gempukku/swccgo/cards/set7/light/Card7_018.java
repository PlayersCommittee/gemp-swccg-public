package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
 * Subtype: Rebel
 * Title: General McQuarrie
 */
public class Card7_018 extends AbstractRebel {
    public Card7_018() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "General McQuarrie", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Commander from Ralltiir. Fled from his homeworld after its occupation by the Empire. Was instrumental in the establishment of the new Rebel base on Hoth.");
        setGameText("While McQuarrie is on Hoth, your Hoth Sentry is not unique (•), is doubled, deploys free, applies all of its modifiers and is immune to Alter. Power -1 when not on Hoth.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.COMMANDER);
        addPersona(Persona.MCQUARRIE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onHothCondition = new OnCondition(self, Filters.McQuarrie, Title.Hoth);
        Filter hothSentryFilter = Filters.and(Filters.your(self), Filters.Hoth_Sentry);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, hothSentryFilter, onHothCondition));
        modifiers.add(new DoubledModifier(self, hothSentryFilter, onHothCondition));
        modifiers.add(new DeploysFreeModifier(self, hothSentryFilter, onHothCondition));
        modifiers.add(new ModifyGameTextModifier(self, hothSentryFilter, onHothCondition, ModifyGameTextType.HOTH_SENTRY__APPLIES_ALL_MODIFIERS));
        modifiers.add(new ImmuneToTitleModifier(self, hothSentryFilter, onHothCondition, Title.Alter));
        modifiers.add(new PowerModifier(self, new NotCondition(onHothCondition), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        //Excluded From Battle - special rules exception:
        //"being excluded will not cause ... other cards to be canceled or otherwise removed from table"
        Condition onHothCondition = new OnCondition(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.McQuarrie, Title.Hoth);
        Filter hothSentryFilter = Filters.and(Filters.your(self), Filters.Hoth_Sentry);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        //prevent Hoth Sentry from being removed from table
        modifiers.add(new NotUniqueModifier(self, hothSentryFilter, new AndCondition(onHothCondition, new IsOnlyExcludedCondition(self))));
        return modifiers;
    }
}
