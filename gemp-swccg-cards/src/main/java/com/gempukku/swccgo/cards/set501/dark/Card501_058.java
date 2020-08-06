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
        setGameText("Deploy Maul’s Chambers. " +
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
        Filter cardsThatMayNotDeploy = Filters.and(Filters.or(Filters.hasAbilityOrHasPermanentPilotWithAbility, Filters.not(Filters.or(independentStarships, v13Maul, Filters.alien))));
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

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, 4, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Crimson Dawn"), Filters.loreContains("Hutt")))) {

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