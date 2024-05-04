package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Executor: Holotheatre
 */
public class Card4_161 extends AbstractUniqueStarshipSite {
    public Card4_161() {
        super(Side.DARK, Title.Holotheatre, Persona.EXECUTOR, ExpansionSet.DAGOBAH, Rarity.R);
        setLocationDarkSideGameText("If Vader or Emperor on table, your Force generation is +1 here (+2 if both).");
        setLocationLightSideGameText("If you control, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition vaderOnTable = new OnTableCondition(self, Filters.Vader);
        Condition emperorOnTable = new OnTableCondition(self, Filters.Emperor);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, new OrCondition(vaderOnTable, emperorOnTable),
                new ConditionEvaluator(1, 2, new AndCondition(vaderOnTable, emperorOnTable)), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List< Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}