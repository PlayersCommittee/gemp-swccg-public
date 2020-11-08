package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Twilight Is Upon Me (V)
 */
public class Card213_048 extends AbstractNormalEffect {
    public Card213_048() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Twilight Is Upon Me", Uniqueness.UNIQUE);
        setLore("When a Jedi dies, the spirit spreads through the Force and touches the living.");
        setGameText("If He Is The Chosen One on table, deploy on table. You may not deploy [E1] sites. While Luke with Prophecy Of The Force, your Force generation is +1 and opponent's is -1. During your deploy phase, may [download] Yoda's Hut. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.He_Is_The_Chosen_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition lukeWithProphecyCondition = new OnTableCondition(self, Filters.and(Filters.Luke, Filters.at(Filters.hasAttached(Filters.Prophecy_Of_The_Force))));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalForceGenerationModifier(self, lukeWithProphecyCondition, 1, playerId));
        modifiers.add(new TotalForceGenerationModifier(self, lukeWithProphecyCondition, -1, opponent));
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Filters.icon(Icon.EPISODE_I), Filters.site), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.TWILIGHT_IS_UPON_ME__UPLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Yoda's Hut from Reserve Deck");
            action.setActionMsg("Deploy Yoda's Hut from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Yodas_Hut, true));
            actions.add(action);
        }

        return actions;
    }
}