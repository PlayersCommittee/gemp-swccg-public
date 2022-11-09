package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Creature
 * Title: Dianoga
 */
public class Card2_110 extends AbstractCreature {
    public Card2_110() {
        super(Side.DARK, 3, 4, null, 5, 0, Title.Dianoga, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("'Garbage squid' from Vodran's jungles. Changes color to match last meal. When unfed, turns transparent. Eats almost anything. Flexible eyestalk. 7 tentacles. Up to 6 meters long.");
        setGameText("* Ferocity = (power/ferocity of last character or creature eaten) + destiny. Habitat: exterior Dagobah sites, Trash Compactor and Dark Waters.");
        addModelType(ModelType.SWAMP);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.exterior_Dagobah_site, Filters.Trash_Compactor, Filters.sameSiteAs(self, Filters.Dark_Waters));
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
}
