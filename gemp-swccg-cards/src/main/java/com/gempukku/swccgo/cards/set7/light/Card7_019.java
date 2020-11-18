package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Grondorn Muse
 */
public class Card7_019 extends AbstractRebel {
    public Card7_019() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, "Grondorn Muse", Uniqueness.UNIQUE);
        setLore("Formerly belonged to the Corellian militia. Popular musician before the Empire blacklisted his songs for their political content. Joined the Alliance with his wife, Duriet.");
        setGameText("While Grondorn is on Yavin 4, your Yavin Sentry is not unique (•), is doubled, deploys free, applies all three of its modifiers and is immune to Alter. Power -1 when not on Yavin 4.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onYavin4Condition = new OnCondition(self, Filters.title("Grondorn Muse"), Title.Yavin_4);
        Filter yavinSentryFilter = Filters.and(Filters.your(self), Filters.Yavin_Sentry);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, yavinSentryFilter, onYavin4Condition));
        modifiers.add(new DoubledModifier(self, yavinSentryFilter, onYavin4Condition));
        modifiers.add(new DeploysFreeModifier(self, yavinSentryFilter, onYavin4Condition));
        modifiers.add(new ModifyGameTextModifier(self, yavinSentryFilter, onYavin4Condition, ModifyGameTextType.YAVIN_SENTRY__APPLIES_ALL_MODIFIERS));
        modifiers.add(new ImmuneToTitleModifier(self, yavinSentryFilter, onYavin4Condition, Title.Alter));
        modifiers.add(new PowerModifier(self, new NotCondition(onYavin4Condition), -1));
        return modifiers;
    }
}
