package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableModifier;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableMultiplierModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: General Dodonna
 */
public class Card1_010 extends AbstractRebel {
    public Card1_010() {
        super(Side.LIGHT, 2, 3, 2, 2, 5, Title.General_Dodonna, Uniqueness.UNIQUE);
        setLore("Skilled tactician and natural leader. Planned attack on Death Star after analyzing technical readouts provided by Princess Leia. Star Destroyer captain during Old Republic.");
        setGameText("Increases Rebel Planners by 1, doubles if at same site. May use 1 Force to cancel Wrong Turn. Each Rebel present with him at a Yavin 4 site is power +1.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CalculationVariableModifier(self, Filters.Rebel_Planners,
                new NotCondition(new AtSameSiteAsCondition(self, Filters.Rebel_Planners)), 1));
        modifiers.add(new CalculationVariableMultiplierModifier(self, Filters.Rebel_Planners,
                new AtSameSiteAsCondition(self, Filters.Rebel_Planners), 2));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.presentWith(self)),
                new AtCondition(self, Filters.Yavin_4_site), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTargetToCancel(game, self, Filters.Wrong_Turn)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Wrong_Turn, Title.Wrong_Turn, 1);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Wrong_Turn)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect, 1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
