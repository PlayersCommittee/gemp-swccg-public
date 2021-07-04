package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 15
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself (V)
 */
public class Card215_017 extends AbstractObjective {
    public Card215_017() {
        super(Side.LIGHT, 0, Title.Rescue_The_Princess);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Central Core, A Power Loss, Detention Block Corridor (with Prisoner 2187 imprisoned there), and Trash Compactor." +
                "For remainder of game, Path Of Least Resistance is canceled. Your Death Star sites generate +1 Force for you and are immune to Set Your Course For Alderaan. Death Star sites may not be converted. You may not deploy Luke of ability > 4 or [Episode I] (or [Episode VII]) Jedi." +
                "Flip this card if Leia is present at a Death Star site and A Power Loss has been 'shut down' this game.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameState gameState = game.getGameState();

        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Death_Star_Central_Core, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Central Core to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.A_Power_Loss, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose A Power Loss to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Trash_Compactor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Trash Compactor to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Detention_Block_Corridor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Detention Block to deploy";
                    }
                });

        if (Filters.canSpot(gameState.getReserveDeck(playerId), game, Filters.Detention_Block_Corridor)) {
            action.appendRequiredEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Prisoner_2187, Filters.Detention_Block_Corridor, true, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), DeployAsCaptiveOption.deployAsImprisonedCaptive(), false) {
                        @Override
                        public String getChoiceText() {
                            return "Choose Prisoner 2187 to deploy";
                        }
                    });
        }

        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourDeathStarSites = Filters.and(Filters.your(playerId), Filters.Death_Star_site);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceGenerationModifier(self, yourDeathStarSites, 1, playerId));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.Luke, Filters.abilityMoreThan(4)), Filters.and(Filters.Jedi, Filters.or(Filters.icon(Icon.EPISODE_I), Filters.icon(Icon.EPISODE_VII)))), playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Set_Your_Course_For_Alderaan, ModifyGameTextType.SET_YOUR_COURSE_FOR_ALDERAAN__ONLY_AFFECTS_DARK_SIDE_DEATH_STAR_SITES));
        modifiers.add(new MayNotBeConvertedModifier(self, Filters.Death_Star_site));
        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Path_Of_Least_Resistance))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        if (GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Leia, Filters.presentAt(Filters.Death_Star_site)))
                && GameConditions.isDeathStarPowerShutDown(game)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.appendEffect(
                    new FlipCardEffect(action, self)
            );
            actions.add(action);
        }

        return actions;
    }
}
