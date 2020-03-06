package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Interrupt
 * Subtype: Used
 * Title: Vader's Anger (V) (Errata)
 */
public class Card501_050 extends AbstractUsedInterrupt {
    public Card501_050() {
        super(Side.DARK, 5, "Vader's Anger", Uniqueness.UNIQUE);
        setLore("Anger and aggression fuel the dark side of the Force.");
        setGameText("If Vader’s Lightsaber just 'hit' a character, cancel that character's game text (if it missed, swing again at same target). OR Cancel It's A Trap! at a Dark Jedi’s site. OR A revel trooper in battle with Vader is lost. [Immune to Sense]");
        addIcons(Icon.TATOOINE, Icon.VIRTUAL_SET_11);
        addImmuneToCardTitle(Title.Sense);
        setVirtualSuffix(true);
        setTestingText("Vader's Anger (V) (Errata)");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Rebel, Filters.trooper))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target rebel trooper to be lost", Filters.and(Filters.Rebel, Filters.trooper, Filters.with(self, Filters.Vader))) {
                        @Override
                        protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, targetedCard)
                            );
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.lightsaber, Filters.Vader)) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel character's gametext.");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelGameTextUntilEndOfBattleEffect(action, cardHit));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Its_A_Trap)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Dark_Jedi)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }
}