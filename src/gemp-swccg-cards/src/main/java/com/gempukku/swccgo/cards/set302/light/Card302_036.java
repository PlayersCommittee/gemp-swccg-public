package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
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
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.common.Keyword;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Creature
 * Title: Proto-Cythraul
 */
public class Card302_036 extends AbstractCreature {
    public Card302_036() {
        super(Side.LIGHT, 3, 4, null, 5, 0, Title.Proto_Cythraul, Uniqueness.RESTRICTED_3, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("The Proto-Cythraul is a genetically enhanced version of a standard Cythraul. Using Ascendant crystal technology, the Proto-Cythraul is faster and far more durable than its original counterpart.");
        setGameText("* Ferocity = (power/ferocity of last character or creature eaten) + destiny. Habitat: exterior planet sites. If defeated, opponent may draw destiny and retrieve Force equal to destiny draw.");
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
        modifiers.add(new DefinedByGameTextFerocityModifier(self, new InPlayDataAsFloatEvaluator(self), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.or(Filters.character, Filters.creature), self)) {
            EatenResult eatenResult = (EatenResult) effectResult;
            float value = Filters.character.accepts(game, eatenResult.getCardEaten()) ? eatenResult.getPower() : eatenResult.getFerocity();
            self.setWhileInPlayData(new WhileInPlayData(value));
        }
        return null;
    }
	
	@Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDefeatedBy(game, effectResult, self, Filters.any)
                && GameConditions.canDrawDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Draw destiny to retrieve Force");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null) {
                                return;
                            }
                            action.appendEffect(
                                    new RetrieveForceEffect(action, playerId, totalDestiny));
                        }
                    });
            return Collections.singletonList(action);
        }
		return null;
	}
}