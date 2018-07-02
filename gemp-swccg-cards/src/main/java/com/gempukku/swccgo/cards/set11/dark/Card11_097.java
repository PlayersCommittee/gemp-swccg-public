package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractPodracer;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerRaceTotalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawRaceDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.RaceDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Podracer
 * Title: Sebulba's Podracer
 */
public class Card11_097 extends AbstractPodracer {
    public Card11_097() {
        super(Side.DARK, 3, Title.Sebulbas_Podracer);
        setLore("Some hidden modifications mean Sebulba's Podracer doesn't conform to race specifications. The duplicitous Dug has equipped it with a flame emitter that can fry other Podracers.");
        setGameText("Deploy on Podrace Arena. Adds 3 to each of your race destinies here. If you and your opponent have the same race total, may use 2 Force. Opponent draws no race destiny for remainder of turn (may only be used once for any race total).");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Podrace_Arena;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new RaceDestinyModifier(self, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(self)), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.SEBULBAS_PODRACER__OPPONENT_DRAWS_NO_RACE_DESTINY;

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            float yourRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, playerId);
            float opponentsRaceTotal = modifiersQuerying.getHighestRaceTotal(gameState, opponent);
            if (yourRaceTotal == opponentsRaceTotal) {
                if (GameConditions.isOncePerRaceTotal(game, self, yourRaceTotal, gameTextActionId)
                        && GameConditions.canUseForce(game, playerId, 2)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Make opponent draw no race destiny");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerRaceTotalEffect(action, yourRaceTotal));
                    // Pay cost(s)
                    action.appendCost(
                            new UseForceEffect(action, playerId, 2));
                    // Perform result(s)
                    action.appendEffect(
                            new AddUntilEndOfTurnModifierEffect(action, new MayNotDrawRaceDestinyModifier(self, opponent),
                                    "Makes opponent draw no race destiny"));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}