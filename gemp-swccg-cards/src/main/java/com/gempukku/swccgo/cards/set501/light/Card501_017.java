package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Objective
 * Title: He Is The Chosen One / He Will Bring Balance
 */
public class Card501_017 extends AbstractObjective {
    public Card501_017() {
        super(Side.LIGHT, 0, Title.He_Is_The_Chosen_One);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Jedi Council Chamber (with Prophecy Of The Force there), Ewok Village, and I Feel The Conflict.\n" +
                "For remainder of game, you may not deploy [Episode I] or [Episode VII] characters (except Obi-Wan and Yoda) or locations. Luke may not be captured by Bring Him Before Me unless there are two cards stacked on Insignificant Rebellion during any move phase.\n" +
                "While this side up, you may initiate battles for free.\n" +
                "Flip this card if Luke (or a Jedi) at a battleground site and opponent has no characters of ability > 4 at battleground sites.");
        addIcons(Icon.VIRTUAL_SET_8);
        setTestingText("He Is The Chosen One (ERRATA)");
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Council_Chamber, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Jedi Council Chamber to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToTargetFromReserveDeckEffect(action, Filters.Prophecy_Of_The_Force, Filters.Jedi_Council_Chamber, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Prophecy Of The Force to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Ewok_Village, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Ewok Village to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.I_Feel_The_Conflict, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose I Feel The Conflict to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.and(Filters.or(Icon.EPISODE_I, Icon.EPISODE_VII), Filters.or(Filters.and(Filters.character, Filters.except(Filters.or(Filters.ObiWan, Filters.Yoda))), Filters.location)), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ModifyGameTextModifier(self, Filters.Bring_Him_Before_Me, new UnlessCondition(new AndCondition(new PhaseCondition(Phase.MOVE),
                                new OnTableCondition(self, Filters.and(Filters.Insignificant_Rebellion, Filters.hasStacked(2, Filters.any))))),
                                ModifyGameTextType.BRING_HIM_BEFORE_ME__MAY_NOT_CAPTURE_LUKE), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.or(Filters.Luke, Filters.Jedi), Filters.at(Filters.battleground_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(4), Filters.at(Filters.battleground_site)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
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