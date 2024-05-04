package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: No Bargain
 */
public class Card601_149 extends AbstractNormalEffect {
    public Card601_149() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.No_Bargain, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Zeet tu seet. Jabba no tuzindy honkabee.'");
        setGameText("Deploy on your [Reflections II] Objective; 'Xizor' on it (and on Guri) is treated as 'Shada'. Non-[Permanent Weapon] alien females are Mistryl and Black Sun agents. You may not deploy cards with ability except Mistryl and [Independent Starship] starships. Mistryl (except Kitik) are power +1 (+2 if unique (*)), and, while Scum And Villainy on table, deploy +1. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.hasAttached(self), Filters.Guri), ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.not(Icon.PERMANENT_WEAPON), Filters.alien, Filters.female), Keyword.MISTRYL));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.not(Icon.PERMANENT_WEAPON), Filters.alien, Filters.female), Keyword.BLACK_SUN_AGENT));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.except(Filters.Mistryl), Filters.except(Filters.and(Icon.INDEPENDENT, Filters.starship))), self.getOwner()));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Mistryl, Filters.except(Filters.title("Kitik Keed'kak"))), new CardMatchesEvaluator(1, 2, Filters.unique)));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.Mistryl, Filters.except(Filters.title("Kitik Keed'kak"))), new OnTableCondition(self, Filters.Scum_And_Villainy), 1));
        return modifiers;
    }
}