package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Objective
 * Title: The Shield Will Be Down In Moments / Imperial Troops Have Entered The Base! (AI)
 */
public class Card222_030 extends AbstractObjective {
    public Card222_030() {
        super(Side.DARK, 0, Title.The_Shield_Will_Be_Down_In_Moments, ExpansionSet.SET_22, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setAlternateImageSuffix(true);
        setGameText("Deploy 5th Marker, [Set 17] 4th Marker, 1st Marker, and [Set 9] Prepare For A Surface Attack." +
                "For remainder of game, you may not deploy Rebel Base Occupation, Sunsdown, or Dark Jedi (except Vader). " +
                "AT-AT cannons may not fire during your control phase. Echo Base Sensors is canceled." +
                "While this side up, once per turn, may [download] a Hoth location. " +
                "Opponent loses no more than 1 Force to You May Start Your Landing." +
                "Flip this card if Main Power Generators 'blown away.'");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Fifth_Marker, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose 5th Marker to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.VIRTUAL_SET_17, Filters.Fourth_Marker), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Set 17] 4th marker to deploy";
                    }
                });

        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.First_Marker, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose 1st Marker to deploy";
                    }
                });

        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.VIRTUAL_SET_9, Filters.Prepare_For_A_Surface_Attack), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Set 9] Prepare for a Surface Attack to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeFiredModifier(self, Filters.and(Filters.your(self), Filters.AT_AT_Cannon), new PhaseCondition(Phase.CONTROL, playerId)), "AT-AT Cannons can't be fired"));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Rebel_Base_Occupation, Filters.Sunsdown, Filters.and(Filters.Dark_Jedi, Filters.not(Filters.Vader))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new CancelsGameTextModifier(self, Filters.title("Echo Base Sensors")), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LimitForceLossFromCardModifier(self, Filters.You_May_Start_Your_Landing, 1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_SHIELD_WILL_BE_DOWN_IN_MOMENTS__DOWNLOAD_HOTH_LOCATION;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Hoth location from Reserve Deck");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Hoth_location, true));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.canBeFlipped(game, self)
                && TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.title(Title.Main_Power_Generators, true))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}