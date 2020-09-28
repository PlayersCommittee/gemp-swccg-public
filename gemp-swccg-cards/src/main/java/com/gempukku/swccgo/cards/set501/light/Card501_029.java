package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Twilight Is Upon Me (V)
 */
public class Card501_029 extends AbstractNormalEffect {
    public Card501_029() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Twilight Is Upon Me", Uniqueness.UNIQUE);
        setLore("When a Jedi dies, the spirit spreads through the Force and touches the living.");
        setGameText("If He Is The Chosen One on table, deploy on table. During your turn, may take a [Dag] or Death Star II site into hand from Reserve Deck; reshuffle. Opponent may not modify or cancel your Force drains or battle destiny draws with Prophecy Of The Force. [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
        setTestingText("Twilight Is Upon Me (V)");
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.He_Is_The_Chosen_One);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        Filter locationWithProphecy = Filters.and(Filters.hasAttached(Filters.Prophecy_Of_The_Force));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, locationWithProphecy, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, locationWithProphecy, opponent, playerId));
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, locationWithProphecy, playerId, opponent));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, locationWithProphecy, playerId, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.TWILIGHT_IS_UPON_ME__UPLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a [Dagobah] Site or Death Star II site into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.icon(Icon.DAGOBAH), Filters.site), Filters.Death_Star_II_site), true));
            actions.add(action);
        }

        return actions;
    }
}