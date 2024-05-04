package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: WED-1016 'Techie' Droid
 */
public class Card3_024 extends AbstractDroid {
    public Card3_024() {
        super(Side.LIGHT, 3, 2, 0, 3, "WED-1016 'Techie' Droid", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.C1);
        setLore("Cybot Galactica starship maintenance droid. Repairs over 5,000 different onboard systems. Used by Rebel Alliance salvage teams.");
        setGameText("While at an exterior planet site or docking bay, once per turn may lose 1 Force to place a 'hit' starship or vehicle at same site, adjacent site, related system or related cloud sector in Used Pile instead of Lost Pile.");
        addIcons(Icon.HOTH);
        addModelType(ModelType.MAINTENANCE);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter starshipOrVehicleFilter = Filters.and(Filters.your(self), Filters.hit, Filters.or(Filters.starship, Filters.vehicle),
                Filters.or(Filters.atSameOrAdjacentSite(self), Filters.at(Filters.or(Filters.relatedSystem(self), Filters.relatedCloudSector(self)))));
        Filter exteriorPlanetSiteOrDockingBay = Filters.or(Filters.exterior_planet_site, Filters.docking_bay);

        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, starshipOrVehicleFilter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, exteriorPlanetSiteOrDockingBay)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardToBeLost) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeLost) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, result.getCardToBeLost()));
                        }
                    });
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, starshipOrVehicleFilter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAtLocation(game, self, exteriorPlanetSiteOrDockingBay)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardToBeForfeited) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardToBeForfeited) + " in Used Pile when forfeited");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getForfeitCardEffect().setForfeitToUsedPile();
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
