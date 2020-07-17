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
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
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
        super(Side.DARK, 0, "Shadow Collective");
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Maul’s Chambers. May deploy Reception Area. For remainder of game you may not deploy cards with ability (or [P]) except capital [Ind] starships, [Ep 1] bounty hunters, assassins, gangsters, and characters with Black Sun, Crimson Dawn, or Hutt in lore. Once per turn, may deploy a non-unique blaster or a card with First Light in title from Reserve Deck; reshuffle. Flip this card if you control 2 battlegrounds (and opponent controls no battleground site) during your control phase OR If you just 'hit' a character.");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("Shadow Collective");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter independentCapitals = Filters.and(Icon.INDEPENDENT, Filters.capital_starship);
        Filter ep1BountyHunters = Filters.and(Icon.EPISODE_I, Filters.bounty_hunter);
        Filter loreCharacters = Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Crimson Dawn"), Filters.loreContains("Hutt"));
        Filter keywordCharacters = Filters.or(Keyword.ASSASSIN, Keyword.GANGSTER);
        Filter cardsThatMayNotDeploy = Filters.and(Filters.or(Filters.hasAbilityOrHasPermanentPilotWithAbility, Icon.PRESENCE),
                Filters.not(Filters.or(independentCapitals, ep1BountyHunters, loreCharacters, keywordCharacters)));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.your(self.getOwner()), cardsThatMayNotDeploy), self.getOwner()));
        return modifiers;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title("Dathomir: Maul's Chambers"), true, false) {

                });
        action.appendRequiredEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.title("First Light: Reception Area"), 0, 1, true, false) {
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Reception Area to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.non_unique, Filters.blaster), Filters.titleContains("First Light")), true)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.controls(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground)
                && !GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_site)) {

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