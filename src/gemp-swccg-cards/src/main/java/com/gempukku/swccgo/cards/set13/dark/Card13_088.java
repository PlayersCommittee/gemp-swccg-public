package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Used
 * Title: The Ebb Of Battle
 */
public class Card13_088 extends AbstractUsedInterrupt {
    public Card13_088() {
        super(Side.DARK, 5, "The Ebb Of Battle", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Lightsaber confrontations are a complex dance of feints, strikes, parries and footwork. Mistakes are rarely forgiven.");
        setGameText("Activate 1 Force. OR Add 1 to your just-drawn duel destiny. OR If under your Dark Jedi as one of that character's combat cards, reveal to opponent and place in your Lost Pile to cancel an opponent's Force drain. (Immune to Sense.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    /**
     * Allow Force drain cancel action to be checked while this card is stacked face down as a combat card.
     */
    @Override
    public boolean visitCardWhenStackedFaceDown() {
        return true;
    }

    /**
     * Action 1: Activate 1 Force. Not playable while stacked face down (combat-card visit opt-in).
     */
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        if (self.getZone() == Zone.STACKED_FACE_DOWN) {
            return null;
        }
        if (!GameConditions.canActivateForce(game, playerId)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setImmuneTo(Title.Sense);
        action.setText("Activate 1 Force");
        // Allow response(s)
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new ActivateForceEffect(action, playerId, 1));
                    }
                }
        );
        return Collections.singletonList(action);
    }

    /**
     * Action 2: Add 1 to your just-drawn duel destiny. Not playable while stacked face down.
     */
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        if (self.getZone() == Zone.STACKED_FACE_DOWN) {
            return null;
        }

        // Check condition(s)
        if (TriggerConditions.isDuelDestinyJustDrawnBy(game, effectResult, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Add 1 to duel destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * Action 3: While under your Dark Jedi as a combat card, reveal and place in Lost Pile to cancel opponent's Force drain.
     * Card-local; uses existing while-stacked interrupt hooks (no MayPlayAsIfFromHand / shared stacked-reveal hierarchy).
     */
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActionsWhenStacked(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Must be a combat card under your Dark Jedi
        if (!self.isCombatCard()) {
            return null;
        }
        final PhysicalCard stackedOn = self.getStackedOn();
        if (stackedOn == null || !Filters.and(Filters.your(self), Filters.Dark_Jedi).accepts(game, stackedOn)) {
            return null;
        }

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Reveal and lose combat card to cancel Force drain");
            // Cost moves this card to Lost Pile; skip VOID relocation during play resolution
            action.setRemovedFromZone(true);
            action.appendCost(
                    new ShowCardOnScreenEffect(action, self));
            action.appendCost(
                    new PutStackedCardInLostPileEffect(action, playerId, self, false));
            action.allowResponses("Cancel Force drain",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
