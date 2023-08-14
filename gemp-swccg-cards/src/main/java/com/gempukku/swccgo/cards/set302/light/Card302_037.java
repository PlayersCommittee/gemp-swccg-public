package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.CloseSpaceSlugMouthEffect;
import com.gempukku.swccgo.logic.effects.OpenSpaceSlugMouthEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.MayAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;
import com.gempukku.swccgo.logic.modifiers.CancelForceIconModifier;
import com.gempukku.swccgo.common.Keyword;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Creature
 * Title: Crystal Raptor
 */
public class Card302_037 extends AbstractCreature {
    public Card302_037() {
        super(Side.LIGHT, 4, 5, null, 5, 0, "Crystal Raptor", Uniqueness.RESTRICTED_3, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Crystal Raptor, a Ascendant crystla infused monstorous reptile haunts the waking dreams of many a Brotherhood member who faced one in the field.");
        setGameText("* Ferocity = two destiny. Habitat: exterior planet sites. Cumulatively absorbs (temporarily cancels) one [dark side] icon present. ");
        addModelType(ModelType.CRYSTAL);
        addIcon(Icon.SELECTIVE_CREATURE);
		addKeywords(Keyword.CRYSTAL_CREATURE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.exterior_planet_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 2));
		modifiers.add(new CancelForceIconModifier(self, Filters.wherePresent(self), 1, Icon.DARK_FORCE, true));
        return modifiers;
    }
}
