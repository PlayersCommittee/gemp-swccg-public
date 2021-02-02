package com.gempukku.swccgo.cards.set111.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Third Anthology)
 * Type: Objective
 * Title: Set Your Course For Alderaan / The Ultimate Power In The Universe
 */
public class Card111_006 extends AbstractObjective {
    public Card111_006() {
        super(Side.DARK, 0, Title.Set_Your_Course_For_Alderaan);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Death Star and Alderaan systems and Docking Bay 327. For remainder of game, Revolution is canceled and Commence Primary Ignition may target only Alderaan, Yavin 4, Hoth or a Subjugated planet. While this side up, once during each of your deploy phases, may take one card with 'Death Star' in title into hand from Reserve Deck; reshuffle. You may not Force drain at Alderaan system. At Death Star sites, your Force drains and battle damage against you are canceled. Flip this card if Alderaan is 'blown away.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Death_Star_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Death Star system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Deployable_By_SYCFA, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose location to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Docking_Bay_327, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Docking Bay 327 to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isPlayingCard(game, effect, Filters.Revolution)
                                        && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

                                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                                    actions.add(action);
                                }
                                return actions;
                            }
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isTableChanged(game, effectResult)
                                        && GameConditions.canTargetToCancel(game, self, Filters.Revolution)) {

                                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardAction(action, Filters.Revolution, Title.Revolution);
                                    actions.add(action);
                                }
                                return actions;
                            }
                        }
                ));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeTargetedByModifier(self, Filters.and(Filters.planet_system, Filters.not(Filters.or(Filters.Alderaan_system,
                                Filters.Yavin_4_system, Filters.Hoth_system, Filters.Subjugated_system))), Filters.Commence_Primary_Ignition), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SET_YOUR_COURSE_FOR_ALDERAAN__UPLOAD_CARD_WITH_DEATH_STAR_IN_TITLE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with 'Death Star' in title into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.titleContains("Death Star"), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Alderaan_system, playerId));
        modifiers.add(new NoBattleDamageModifier(self, Filters.Death_Star_site, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.Death_Star_site)
                && GameConditions.canCancelForceDrain(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Alderaan, true)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}