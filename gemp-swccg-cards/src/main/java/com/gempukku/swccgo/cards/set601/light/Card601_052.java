package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, Strong In The Force
 */
public class Card601_052 extends AbstractRebel {
    public Card601_052() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Luke Skywalker, Strong In The Force", Uniqueness.UNIQUE);
        setLore("Luke's experience on Dagobah gave him great skill in using the Force. Vader had to keep his focus on Luke at all times, or face the consequences.");
        setGameText("Adds 3 to power of anything he pilots.  While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled.  When Luke leaves table, place all your cards on him in Used Pile.  Immune to attrition < 4.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6, Icon.TATOOINE);
        addPersona(Persona.LUKE);
        addKeywords(Keyword.RED_SQUADRON, Keyword.ROGUE_SQUADRON);
        setMatchingStarshipFilter(Filters.or(Filters.Red_5, Filters.Rogue_1));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        //maybe not the best way to do this, but it works
        Collection<PhysicalCard> attached = Filters.filter(self.getCardsPreviouslyAttached(), game, Filters.your(self));

        if (!attached.isEmpty()) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place your cards on Luke in Used Pile");
            action.appendEffect(new PlaceCardsInUsedPileFromOffTableEffect(action, attached));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if ((GameConditions.isPiloting(game, self, Filters.or(Filters.Red_5, Filters.Rogue_1))
                || GameConditions.isArmedWith(game, self, Filters.Lukes_Lightsaber))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)) {

            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if ((GameConditions.isPiloting(game, self, Filters.or(Filters.Red_5, Filters.Rogue_1))
                || GameConditions.isArmedWith(game, self, Filters.Lukes_Lightsaber))
                && TriggerConditions.isPlayingCard(game, effect, Filters.I_Have_You_Now)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if ((GameConditions.isPiloting(game, self, Filters.or(Filters.Red_5, Filters.Rogue_1))
                || GameConditions.isArmedWith(game, self, Filters.Lukes_Lightsaber))
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.I_Have_You_Now)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.I_Have_You_Now, Title.I_Have_You_Now);
            actions.add(action);
        }
        return actions;
    }
}