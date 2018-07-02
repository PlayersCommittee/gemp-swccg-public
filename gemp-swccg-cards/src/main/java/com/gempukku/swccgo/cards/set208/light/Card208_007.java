package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfEffectResultModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotContributeToForceRetrievalModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: Resistance
 * Title: Ilco Munica
 */
public class Card208_007 extends AbstractResistance {
    public Card208_007() {
        super(Side.LIGHT, 3, 3, 3, 3, 4, "Ilco Munica", Uniqueness.UNIQUE);
        setLore("Abednedo scavenger.");
        setGameText("When deployed, may [upload] a non-unique Resistance character. While present on Jakku, if opponent just Force drained at an adjacent site, they retrieve no Force with Graveyard Of Giants.");
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        addKeyword(Keyword.SCAVENGER);
        setSpecies(Species.ABEDNEDO);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ILCO_MUNICA__UPLOAD_NON_UNIQUE_RESISTANCE_CHARACTER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a card into hand from Reserve Deck");
            action.setActionMsg("Take a non-unique Resistance character into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.Resistance_character), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.forceDrainCompleted(game, effectResult, opponent, Filters.adjacentSite(self))
                && GameConditions.isPresent(game, self)
                && GameConditions.isOnSystem(game, self, Title.Jakku)
                && GameConditions.canSpot(game, self, Filters.Graveyard_Of_Giants)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cause opponent to retrieve no Force");
            action.setActionMsg("Cause opponent to retrieve no Force with Graveyard Of Giants");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfEffectResultModifierEffect(action, effectResult,
                            new MayNotContributeToForceRetrievalModifier(self, Filters.Graveyard_Of_Giants),
                            "Causes opponent to retrieve no Force with Graveyard Of Giants"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
