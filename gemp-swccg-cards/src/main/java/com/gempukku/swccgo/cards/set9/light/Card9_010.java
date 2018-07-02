package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Marmor
 */
public class Card9_010 extends AbstractRebel {
    public Card9_010() {
        super(Side.LIGHT, 2, 3, 3, 2, 4, Title.Marmor, Uniqueness.UNIQUE);
        setLore("Corellian dock mechanic. Defected to the Alliance with General Madine. Manages staff of 75 starfighter and 12 star cruiser mechanics in Home One's rear quadrant.");
        setGameText("Reduces X by 3 on Docking And Repair Facilities when a Star Cruiser is held there. When he is at a docking bay where you have just deployed a starfighter, once per turn you may retrieve 1 Force.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.GRAY_SQUADRON);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CalculationVariableModifier(self, Filters.and(Filters.Docking_And_Repair_Facilities, Filters.hasStacked(Filters.Star_Cruiser)),
                -3, Variable.X));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.your(self), Filters.starfighter), Filters.sameLocation(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, Filters.docking_bay)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
