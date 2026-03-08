package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Effect
 * Title: We're Leaving (V)
 */
public class Card204_015 extends AbstractNormalEffect {
    public Card204_015() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "We're Leaving", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Qui-Gon realized that sometimes it's best to just leave, before any more damage is done.");
        setGameText("Deploy on table. If your Podracer is on table and opponent's is not, they generate no Force at Podrace Arena. Once during opponent's control phase, if you are winning (or have won) a Podrace, may stack the bottom card of your Lost Pile on Credits Will Do Fine. [Immune to Alter.]");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_4);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Condition yourPodracerOnTable = new OnTableCondition(self, Filters.and(Filters.your(playerId), Filters.Podracer));
        Condition opponentsPodracerNotOnTable = new NotCondition(new OnTableCondition(self, Filters.and(Filters.your(opponent), Filters.Podracer)));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.Podrace_Arena, new AndCondition(yourPodracerOnTable, opponentsPodracerNotOnTable), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        GameTextActionId gameTextActionId = GameTextActionId.WERE_LEAVING__STACK_CARD_FROM_LOST_PILE_ON_CREDITS;
        GameState gameState = game.getGameState();
        PhysicalCard credits = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);
        PhysicalCard bottomOfLostPile = gameState.getBottomOfCardPile(playerId, Zone.LOST_PILE);

        if (credits != null
                && bottomOfLostPile != null
                && GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && (GameConditions.hasHigherRaceTotal(game, playerId)
                || GameConditions.hasWonPodrace(game, playerId))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack bottom card from Lost Pile");
            action.setActionMsg("Stack bottom card of Lost Pile on " + GameUtils.getCardLink(credits));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, bottomOfLostPile, credits, true, false, false));
            actions.add(action);
        }
        return actions;
    }
}
