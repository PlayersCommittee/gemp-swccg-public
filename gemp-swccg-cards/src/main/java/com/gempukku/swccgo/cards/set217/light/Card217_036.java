package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractAlienRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Alien/Republic
 * Title: Grakchawwaa
 */
public class Card217_036 extends AbstractAlienRepublic {
    public Card217_036() {
        super(Side.LIGHT, 2, 4, 5, 2, 6, "Grakchawwaa", Uniqueness.UNIQUE);
        setLore("Wookiee leader.");
        setGameText("Once per turn, may [download] a bowcaster on your Wookiee at same or adjacent site. During your control phase, if present at a Kashyyyk battleground site and armed with a bowcaster, opponent loses 1 Force.");
        setSpecies(Species.WOOKIEE);
        addKeywords(Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.GRAKCHAWWAA__DEPLOY_BOWCASTER;

        // Once per turn, may deploy a bowcaster on your Wookiee at same or adjacent site from Reserve Deck; reshuffle.

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.at(Filters.sameOrAdjacentSite(self))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy bowcaster from Reserve Deck");
            action.setActionMsg("Deploy a bowcaster on your Wookiee at same or adjacent site from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.bowcaster, Filters.and(Filters.your(self), Filters.Wookiee, Filters.at(Filters.sameOrAdjacentSite(self))), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        String opponent = game.getOpponent(playerId);
        // Check condition(s)
        if (GameConditions.isPresentAt(game, self, Filters.and(Filters.battleground, Filters.Kashyyyk_site))
                && GameConditions.isArmedWith(game, self, Filters.bowcaster)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (GameConditions.isPresentAt(game, self, Filters.and(Filters.battleground, Filters.Kashyyyk_site))
                && GameConditions.isArmedWith(game, self, Filters.bowcaster)
                && TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        return actions;
    }
}
