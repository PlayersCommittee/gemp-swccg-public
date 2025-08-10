package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Effect
 * Title: Navy Of The First Order
 */
public class Card225_024 extends AbstractNormalEffect {
    public Card225_024() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Navy Of The First Order", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("Deploy on table. [First Order] starships are deploy -1 (-2 to D'Qar) and hyperspeed +1. During your deploy phase, may reveal one [First Order] starship from hand to [upload] a First Order pilot (or vice versa) and deploy both simultaneously. [Immune to Alter.]");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.First_Order_starship, -1, Filters.not(Filters.DQar_location)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.First_Order_starship, -2, Filters.DQar_location));
        modifiers.add(new HyperspeedModifier(self, Filters.First_Order_starship, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        final Filter starship = Filters.and(Icon.FIRST_ORDER, Filters.starship);
        final Filter pilot = Filters.and(Filters.First_Order_pilot);
        Filter filter = Filters.and(Filters.or(pilot, starship), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.NAVY_OF_THE_FIRST_ORDER__DEPLOY_FIRST_ORDER_STARSHIP_AND_PILOT;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal starship or pilot from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (pilot.accepts(game, selectedCard)) {
                                action.setActionMsg("Take a [First Order] starship from Reserve Deck and deploy both simultaneously");
                                searchFilter = starship;
                            }
                            else {
                                action.setActionMsg("Take a First Order pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = pilot;
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(selectedCard) + " with " + GameUtils.getCardLink(self)));
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}