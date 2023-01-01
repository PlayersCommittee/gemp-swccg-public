package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MaximumToReduceDeployCostByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Mon Calamari Dockyards
 */
public class Card200_044 extends AbstractNormalEffect {
    public Card200_044() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Mon Calamari Dockyards", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setLore("Admiral Ackbar's hit-and-fade tactics force the Imperial Navy to spread throughout the galaxy in a futile attempt to engage the Rebels.");
        setGameText("Deploy on table. Star Cruisers (except Profundity) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starCruisersExceptProfundity = Filters.and(Filters.Star_Cruiser, Filters.except(Filters.Profundity));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, starCruisersExceptProfundity, -2));
        modifiers.add(new MaximumToReduceDeployCostByModifier(self, starCruisersExceptProfundity, 3));
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, starCruisersExceptProfundity));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, starCruisersExceptProfundity, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, starCruisersExceptProfundity, 4));
        return modifiers;
    }
}