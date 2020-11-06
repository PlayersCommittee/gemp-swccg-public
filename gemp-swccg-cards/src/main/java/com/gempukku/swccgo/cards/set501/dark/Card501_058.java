package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
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
        setGameText("Deploy Maul's Chambers. If Massassi Throne Room on table, may deploy [Set 13] Maul to Maul's Chambers. " +
                "For remainder of game, you may not deploy cards with ability (or [Episode I] droids) except characters with 'Black Sun,' 'Crimson Dawn,' or 'Hutt' in lore, assassins, gangsters, [Episode I] bounty hunters, and [Independent] starships. Once per turn, may deploy a non-unique blaster (or a card with 'First Light' in title) from Reserve Deck; reshuffle. " +
                "Flip this card if you just 'hit' a character (or during your battle phase if your gangsters control two battlegrounds).");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("Shadow Collective");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter independentStarships = Filters.and(Icon.INDEPENDENT, Filters.starship);
        Filter episode1BountyHunters = Filters.and(Filters.icon(Icon.EPISODE_I), Filters.bounty_hunter);
        Filter loreCharacters = Filters.or(Filters.loreContains("Crimson Dawn"), Filters.loreContains("Black Sun"), Filters.loreContains("Hutt"));
        Filter cardsThatMayNotDeploy = Filters.or(Filters.and(Filters.icon(Icon.EPISODE_I), Filters.droid), Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(independentStarships, episode1BountyHunters, Filters.assassin, Filters.gangster, loreCharacters))));
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
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.SHADOW_COLLECTIVE__DOWNLOAD_BLASTER_OR_FIRST_LIGHT_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.non_unique, Filters.blaster), Filters.titleContains("First Light")),
                                    Filters.and(Filters.your(playerId), Filters.alien), Filters.titleContains("First Light"), null, false, true)
            );

            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.BATTLE)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.battleground, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.gangster)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
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

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();

        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && GameConditions.occupies(game, playerId, 3, Filters.battleground)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 force");
            action.setActionMsg("Make opponent lose 1 force");
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 1)
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.BATTLE)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.battleground, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.gangster)) {

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