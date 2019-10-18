package com.gempukku.swccgo.cards.set301.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardPlayedThisTurnByPlayerCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Premium Set
 * Type: Defensive Shield
 * Title: Your Ship? (V)
 */
public class Card301_005 extends AbstractDefensiveShield {
    public Card301_005() {
        super(Side.LIGHT, "Your Ship?");
        setVirtualSuffix(true);
        setLore("Han was not sure if Lando had forgiven him for winning the Millennium Falcon. As the old gamblers' saying about sabacc goes: 'Win the game, lose a friend.'");
        setGameText("Plays on table. Cancels A Dangerous Time and Imperial Supply. Each player may play only one card with 'sabacc' in title each turn. You may cancel an opponent's card with 'sabacc' in title by losing 1 Force from hand.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.A_Dangerous_Time,Filters.Imperial_Supply))
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
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)){
            if (GameConditions.canTargetToCancel(game, self, Filters.A_Dangerous_Time)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.A_Dangerous_Time, Title.A_Dangerous_Time);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters.Imperial_Supply)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Imperial_Supply, Title.Imperial_Supply);
                actions.add(action);
            }
            return actions;
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter cardWithSabaccInTitle = Filters.titleContains("sabacc");

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotPlayModifier(self, cardWithSabaccInTitle, new CardPlayedThisTurnByPlayerCondition(playerId, cardWithSabaccInTitle), playerId));
        modifiers.add(new MayNotPlayModifier(self, cardWithSabaccInTitle, new CardPlayedThisTurnByPlayerCondition(opponent, cardWithSabaccInTitle), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(playerId), Filters.titleContains("sabacc")))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.hasHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendCost(new LoseForceFromHandEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.opponents(playerId), Filters.titleContains("sabacc"));

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, filter)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, "opponent's card with 'sabacc' in title");
            action.appendCost(new LoseForceFromHandEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}