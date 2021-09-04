package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Effect
 * Title: Ubrikkian Industries
 */
public class Card215_027 extends AbstractNormalEffect {
    public Card215_027() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ubrikkian Industries", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on table. During your deploy phase, may reveal an alien with 'vehicle' in game text from hand to [upload] a transport vehicle (or vice versa) and deploy both simultaneously. Your drivers are immune to Clash Of Sabers. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_15);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.driving(Filters.any), Title.Clash_Of_Sabers));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter transportVehicle = Filters.transport_vehicle;
        final Filter alienWithVehicleInGametext = Filters.and(Filters.alien, Filters.or(Filters.gameTextContains("vehicle"), Filters.gameTextContains("vehicles")));
        Filter filter = Filters.and(Filters.or(alienWithVehicleInGametext, transportVehicle), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.UBRIKKIAN_INDUSTRIES__DEPLOY_VEHICLE_OR_ALIEN;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal alien or transport vehicle from hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take a transport vehicle from Reserve Deck to deploy simultaneously with " + GameUtils.getCardLink(selectedCard));
                                searchFilter = transportVehicle;
                            } else {
                                action.setActionMsg("Take an alien with 'vehicle' in game text to deploy simultaneously with " + GameUtils.getCardLink(selectedCard));
                                searchFilter = alienWithVehicleInGametext;
                            }
                            // Perform result(s)
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
