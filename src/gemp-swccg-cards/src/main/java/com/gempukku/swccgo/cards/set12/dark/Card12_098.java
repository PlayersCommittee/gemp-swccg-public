package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Baskol Yeesrim
 */
public class Card12_098 extends AbstractRepublic {
    public Card12_098() {
        super(Side.DARK, 3, 3, 2, 3, 5, "Baskol Yeesrim", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setPolitics(2);
        setLore("Gran senator who is part of the Malastare delegation. Supported Aks Moe's suggestion for a commission to be sent to Naboo.");
        setGameText("Agenda: blockade. While in a senate majority, once during your control phase opponent loses 1 Force for each Naboo location you control.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
        setSpecies(Species.GRAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.BLOCKADE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isInSenateMajority(game, self)) {
            int forceToLose = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Naboo_location, Filters.controls(playerId)));
            if (forceToLose > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), forceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        // Check if reached end of control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isInSenateMajority(game, self)) {
            int forceToLose = Filters.countTopLocationsOnTable(game, Filters.and(Filters.Naboo_location, Filters.controls(playerId)));
            if (forceToLose > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose " + forceToLose + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), forceToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
