package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Objective
 * Title: The Hyperdrive Generator's Gone (V) / We'll Need A New One (V)
 */
public class Card210_025 extends AbstractObjective {
    public Card210_025() {
        super(Side.LIGHT, 0, Title.The_Hyperdrive_Generators_Gone);
        setFrontOfDoubleSidedCard(true);
        setVirtualSuffix(true);
        setGameText("Deploy City Outskirts, Watto's Junkyard, and Credits Will Do Fine. For remainder of game, you may not deploy cards with ability except unique (•) aliens, Republic characters, [Republic] starships, and [Episode I] Jedi. Your Destiny is suspended. You lose no Force from [Reflections II] objectives. While this side up, once per game, may take into hand from Reserve Deck an [Episode I] system. Unless present with Qui-Gon, Maul is immune to attrition. Flip this card if there are four or more cards beneath Credits Will Do Fine.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Wattos_Junkyard, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Watto's Junkyard to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.City_Outskirts, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose City Outskirts to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Credits_Will_Do_Fine, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Credits Will Do Fine to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility,
                                Filters.not(Filters.or(Filters.and(Filters.unique, Filters.alien), Filters.Republic_character,
                                        Filters.Republic_starship, Filters.and(Icon.EPISODE_I, Filters.Jedi)))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployUsingDejarikRulesModifier(self, Filters.hasAbilityWhenUsingDejarikRules, playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new SuspendsCardModifier(self, Filters.Your_Destiny), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new NoForceLossFromCardModifier(self, Filters.and(Filters.Objective, Icon.REFLECTIONS_II), playerId), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_HYPERDRIVE_GENERATORS_GONE__UPLOAD_EPISODE_I_SYSTEM;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Episode I system into hand from Reserve Deck");
            action.setActionMsg("Take Episode I system into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.system, Icon.EPISODE_I), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter maulNotWithQuigon = Filters.and(Filters.Maul, Filters.not(Filters.with(self, Filters.QuiGon)));
        modifiers.add(new ImmuneToAttritionModifier(self, maulNotWithQuigon));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {
            PhysicalCard creditsWillDoFine = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);
            if (creditsWillDoFine != null && GameConditions.hasStackedCards(game, creditsWillDoFine, 4)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
