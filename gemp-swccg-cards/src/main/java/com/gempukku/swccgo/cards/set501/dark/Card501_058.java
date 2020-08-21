package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Objective
 * Title: Shadow Collective / You Know Who I Answer To
 */
public class Card501_058 extends AbstractObjective {
    public Card501_058() {
        super(Side.DARK, 0, Title.Shadow_Collective);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Maul’s Chambers.  If Massassi Throne Room on table, may deploy [Set 13] Maul to Maul's Chambers." +
                "For remainder of game you may not deploy cards with ability except [Ind] starships, [V13] Maul, and aliens." +
                "Once per turn, may deploy a non-unique blaster on your alien or a card with First Light in title from Reserve Deck; reshuffle." +
                "Flip this card if you just 'hit' a character (OR if you have 4 characters with “Black Sun,” “Crimson Dawn,” “Hutt,” in lore on table during your deploy phase).");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("Shadow Collective");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter independentStarships = Filters.and(Icon.INDEPENDENT, Filters.starship);
        Filter v13Maul = Filters.and(Icon.VIRTUAL_SET_13, Filters.Maul);
        Filter cardsThatMayNotDeploy = Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(independentStarships, v13Maul, Filters.alien)));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.your(self.getOwner()), cardsThatMayNotDeploy), self.getOwner()));
        return modifiers;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Dathomir_Mauls_Chambers), true, false) {

                });
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.title(Title.Massassi_Throne_Room))) {
            action.appendOptionalEffect(
                    new DeployCardsToLocationFromReserveDeckEffect(action, Filters.Maul, 0, 1, Filters.title(Title.Dathomir_Mauls_Chambers), true, true) {
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose Maul to deploy";
                        }
                    });
        }
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SHADOW_COLLECTIVE__DOWNLOAD_BLASTER_OR_FIRST_LIGHT_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Reserve Deck");
            ArrayList<String> options = new ArrayList<>();
            options.add("Non-unique blaster");
            options.add("Card with First Light in title");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("What would you like to deploy?", options) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        action.appendEffect(
                                                new DeployCardToTargetFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.blaster), Filters.and(Filters.your(playerId), Filters.alien), true)
                                        );
                                    } else {
                                        action.appendEffect(
                                                new DeployCardFromReserveDeckEffect(action, Filters.titleContains("First Light"), true)
                                        );
                                    }
                                }
                            })
            );

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, 4, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.character, Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Crimson Dawn"), Filters.loreContains("Hutt"))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.your(playerId))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
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