package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: R-3PO (Ar-Threepio)
 */
public class Card3_014 extends AbstractDroid {
    public Card3_014() {
        super(Side.LIGHT, 3, 2, 1, 3, "R-3PO (Ar-Threepio)", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("Designed as a response to the threat of Imperial espionage droids. Joins a droid pool and uncovers spies. Has tattoo reading 'Thank The Maker' on left posterior plating.");
        setGameText("Once each turn, during your control phase, for each opponent's spy present opponent must lose 1 Force (2 if spy is a droid or is Undercover, 4 if both).");
        addIcons(Icon.HOTH);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter spyFilter = Filters.and(Filters.opponents(self), Filters.spy, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, spyFilter)) {
            int totalForceToLose = 0;
            Collection<PhysicalCard> spies = Filters.filterActive(game, self, SpotOverride.INCLUDE_UNDERCOVER, spyFilter);
            for (PhysicalCard spy : spies) {
                int forceToLose = 1;
                if (Filters.droid.accepts(game.getGameState(), game.getModifiersQuerying(), spy)) {
                    forceToLose *= 2;
                }
                if (Filters.undercover_spy.accepts(game.getGameState(), game.getModifiersQuerying(), spy)) {
                    forceToLose *= 2;
                }
                totalForceToLose += forceToLose;
            }
            if (totalForceToLose > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + totalForceToLose + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), totalForceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        final Filter spyFilter = Filters.and(Filters.opponents(self), Filters.spy, Filters.present(self));

        // Check condition(s)
        // Check if reached end of owner's control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, spyFilter)) {
            int totalForceToLose = 0;
            Collection<PhysicalCard> spies = Filters.filterActive(game, self, SpotOverride.INCLUDE_UNDERCOVER, spyFilter);
            for (PhysicalCard spy : spies) {
                int forceToLose = 1;
                if (Filters.droid.accepts(game.getGameState(), game.getModifiersQuerying(), spy)) {
                    forceToLose *= 2;
                }
                if (Filters.undercover_spy.accepts(game.getGameState(), game.getModifiersQuerying(), spy)) {
                    forceToLose *= 2;
                }
                totalForceToLose += forceToLose;
            }
            if (totalForceToLose > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + totalForceToLose + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(self.getOwner()), totalForceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
