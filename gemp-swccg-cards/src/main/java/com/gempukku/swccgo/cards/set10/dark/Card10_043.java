package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Imperial
 * Title: Kir Kanos
 */
public class Card10_043 extends AbstractImperial {
    public Card10_043() {
        super(Side.DARK, 3, 2, 5, 3, 3, "Kir Kanos", Uniqueness.UNIQUE);
        setLore("Fiercely devoted Royal Guard. Feels deeply indebted to those who risk their life for him. Unaware of the extent of Palpatine's atrocities and cruelty.");
        setGameText("When armed with a Force pike, adds one battle destiny. Once during each of your deploy phases, lose 1 Force or place Kanos and cards deployed on him in owner's Used Pile (if Emperor on table, may use 1 Force instead).");
        addIcons(Icon.REFLECTIONS_II, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new ArmedWithCondition(self, Filters.Force_pike), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerId, 1, true));
            actions.add(action);

            // Check condition(s)
            if (GameConditions.canUseForce(game, playerId, 1)
                    && GameConditions.canSpot(game, self, Filters.Emperor)) {

                action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Use 1 Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                actions.add(action);
            }

            action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.USED_PILE));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        // Check if reached end of owner's deploy phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.DEPLOY)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)) {
            boolean useForceIsOption = GameConditions.canUseForce(game, playerId, 1) && GameConditions.canSpot(game, self, Filters.Emperor);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            if (useForceIsOption)
                action.setText("Lose 1 Force, use 1 Force, or place in Used Pile");
            else
                action.setText("Lose 1 Force or place in Used Pile");
            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new LoseForceEffect(action, playerId, 1, true));
            if (useForceIsOption) {
                effectsToChoose.add(new UseForceEffect(action, playerId, 1));
            }
            effectsToChoose.add(new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.USED_PILE));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}
