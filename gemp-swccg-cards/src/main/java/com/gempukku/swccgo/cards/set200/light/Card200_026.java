package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: Don't Do That Again (V)
 */
public class Card200_026 extends AbstractDefensiveShield {
    public Card200_026() {
        super(Side.LIGHT, "Don't Do That Again");
        setVirtualSuffix(true);
        setLore("The Jedi won't tolerate silly behavior for very long.");
        setGameText("Plays on table. Once per game, may [upload] an Immediate Effect. While opponent occupies no battleground systems, Mobilization Points is suspended. 'Missing' on Always Thinking With Your Stomach is treated as 'landspeed = 0 for remainder of turn'.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DONT_DO_THAT_AGAIN__UPLOAD_IMMEDIATE_EFFECT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Immediate Effect into hand from Reserve Deck");
            action.setActionMsg("Take an Immediate Effect into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Immediate_Effect, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Mobilization_Points, new NotCondition(new OccupiesCondition(opponent, Filters.battleground_system))));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Always_Thinking_With_Your_Stomach, ModifyGameTextType.ALWAYS_THINKING_WITH_YOUR_STOMACH__MISSING_TREATED_AS_LANDSPEED_0));
        return modifiers;
    }
}