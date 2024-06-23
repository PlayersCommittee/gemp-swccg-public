package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.UtinniEffectStatus;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RecordUtinniEffectCompletedEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Subtype: Utinni
 * Title: Opal of Madness
 */
public class Card304_074 extends AbstractUtinniEffect {
    public Card304_074() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Opal of Madness", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Discovered by members of Scholae Palatinae the Opal has demonstrated a unique effect on the weak minded and novice Force users. Those that survive describe being driven to madness.");
        setGameText("Deploy on character with ability < 5 not at a Seraph site. Target one [CSP] character at a Seraph site. When Target reaches Utinni Effect, retrieve one lost Force for each Force-Attuned character you have on table, two for each Force-Sensitive or Dark Jedi. Lose Utinni Effect.");
        addKeywords(Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.character, Filters.abilityLessThan(5), Filters.not(Filters.at(Title.Seraph)));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.CSP, Filters.at(Title.Seraph));
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        return Filters.CSP;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {
            float numToRetrieve = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.Force_Attuned_character, Filters.mayContributeToForceRetrieval)) +
                    (2 * Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.or(Filters.Force_Sensitive_character, Filters.Dark_Jedi), Filters.mayContributeToForceRetrieval)));

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Retrieve " + GuiUtils.formatAsString(numToRetrieve) + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.COMPLETED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new RecordUtinniEffectCompletedEffect(action, self));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, numToRetrieve));
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}