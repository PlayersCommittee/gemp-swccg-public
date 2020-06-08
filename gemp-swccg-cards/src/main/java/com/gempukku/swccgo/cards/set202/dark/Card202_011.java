package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Pote Snitkin (V)
 */
public class Card202_011 extends AbstractAlien {
    public Card202_011() {
        super(Side.DARK, 3, 3, 2, 2, 3, "Pote Snitkin", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Skrilling smuggler. Supplied Jabba's henchmen with weapons when he was Hermi Odle's predecessor. An excellent driver.");
        setGameText("While driving a vehicle, adds one battle destiny and your total ability here may not be reduced. At same and related sites, Skrillings are power and forfeit +2, and, once per game, your just-lost transport vehicle may be 'recycled' (place in Used Pile).");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.SKRILLING);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition drivingVehicle = new DrivingCondition(self, Filters.vehicle);
        Filter skrillingsAtSameAndRelatedSite = Filters.and(Filters.Skrilling, Filters.at(Filters.sameOrRelatedSite(self)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, drivingVehicle, 1));
        modifiers.add(new MayNotHaveTotalAbilityReducedModifier(self, Filters.here(self), drivingVehicle, playerId));
        modifiers.add(new PowerModifier(self, skrillingsAtSameAndRelatedSite, 2));
        modifiers.add(new ForfeitModifier(self, skrillingsAtSameAndRelatedSite, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.POTE_SNITKIN__RECYCLE_TRANSPORT_VEHICLE;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.transport_vehicle), Filters.sameOrRelatedSite(self))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justLostCard) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
