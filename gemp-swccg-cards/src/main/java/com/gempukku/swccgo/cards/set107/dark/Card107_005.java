package com.gempukku.swccgo.cards.set107.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.EatenByIsPlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Second Anthology
 * Type: Creature
 * Title: Sarlacc
 */
public class Card107_005 extends AbstractCreature {
    public Card107_005() {
        super(Side.DARK, 2, 4, null, 12, 0, Title.Sarlacc, Uniqueness.UNIQUE, ExpansionSet.SECOND_ANTHOLOGY, Rarity.PV);
        setLore("Very patient predator in the Dune Sea. Tentacles can grab prey up to four meters away. Digests victims for 1000 years. Often fed prisoners by Jabba the Hutt.");
        setGameText("* Ferocity = 4 + destiny. Habitat: Great Pit Of Carkoon. If Sarlacc eats a captive, may retrieve 1 Force for each of your aliens here. Anything eaten by Sarlacc is placed out of play.");
        addModelType(ModelType.ANCIENT_DESERT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Great_Pit_Of_Carkoon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 4, 1));
        modifiers.add(new EatenByIsPlacedOutOfPlayModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justEatenBy(game, effectResult, Filters.any, self)
                && ((EatenResult) effectResult).wasCaptive()) {
            int numToRetrieve = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.alien, Filters.here(self), Filters.mayContributeToForceRetrieval));
            if (numToRetrieve > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve " + numToRetrieve + " Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, numToRetrieve));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
