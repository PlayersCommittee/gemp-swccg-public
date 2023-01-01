package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.UseCalculationForDeployCostModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Dantooine Engineering Corps
 */
public class Card601_255 extends AbstractNormalEffect {
    public Card601_255() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Dantooine Engineering Corps", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("The Alliance carefully chooses docking bays from which to launch limited offensives.");
        setGameText("Deploy on table.  While Dantooine Base Operations or More Dangerous Than You Realize on table, Dantooine sites are immune to [Premium] No Escape, and your Force generation is +1.  While you control a Dantooine site, your squadrons may deploy without replacement for X Force, where X = squadron's power -3. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition dboOnTable = new OnTableCondition(self, Filters.or(Filters.Dantooine_Base_Operations, Filters.More_Dangerous_Than_You_Realize));
        Condition youControlDantooineSite = new ControlsCondition(self.getOwner(), Filters.Dantooine_site);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Dantooine_site, new AndCondition(dboOnTable, new NotCondition(new OnTableCondition(self, Filters.and(Filters.not(Icon.PREMIUM), Filters.title(Title.No_Escape))))), Title.No_Escape));
        modifiers.add(new TotalForceGenerationModifier(self, dboOnTable, 1, self.getOwner()));

        modifiers.add(new MayDeployToLocationModifier(self, Filters.and(Filters.your(self), Filters.squadron), youControlDantooineSite, Filters.location));
        modifiers.add(new UseCalculationForDeployCostModifier(self, Filters.and(Filters.your(self), Filters.squadron), youControlDantooineSite, new SubtractEvaluator(new PowerEvaluator(), 3)) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
                return "Deploy cost = power - 3";
            }
        });
        return modifiers;
    }
}