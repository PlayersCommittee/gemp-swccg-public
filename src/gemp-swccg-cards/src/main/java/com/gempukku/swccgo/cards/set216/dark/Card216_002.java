package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceTopCardOfUsedPileOnTopOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SpeciesModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Alien Mob
 */
public class Card216_002 extends AbstractAlien {
    public Card216_002() {
        super(Side.DARK, 2, 4, 4, 2, 5, "Alien Mob", Uniqueness.DIAMOND_1, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("This card has your Rep's species. When deployed, may retrieve a Rep or place a Rep stacked on your Objective in Used Pile. " +
                "Once per turn, if Fearless And Inventive on table and you just retrieved Force, may place top card of Used Pile on Force Pile.");
        addIcons(Icon.VIRTUAL_SET_16);
        addIcon(Icon.WARRIOR, 3);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new SpeciesModifier(self, true));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new ArrayList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        PhysicalCard rep = game.getGameState().getRep(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && rep != null) {

            if (GameConditions.hasLostPile(game, playerId)) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retrieve " + GameUtils.getCardLink(rep));
                action.setActionMsg("Retrieve " + GameUtils.getCardLink(rep));
                // Perform result(s)
                action.appendEffect(
                        new RetrieveCardEffect(action, playerId, Filters.sameTitle(rep)));
                actions.add(action);
            }

            if (GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.Objective, Filters.hasStacked(Filters.sameTitle(rep))))) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place stacked " + GameUtils.getCardLink(rep) + " in Used Pile");
                action.setActionMsg("Place stacked " + GameUtils.getCardLink(rep) + " in Used Pile");
                // Perform result(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, Filters.and(Filters.your(playerId), Filters.Objective), Filters.sameTitle(rep)) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendEffect(
                                        new PutStackedCardInUsedPileEffect(action, playerId, selectedCard, false));
                            }
                        }
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.Fearless_And_Inventive)
                && GameConditions.hasUsedPile(game, playerId)
                && TriggerConditions.justRetrievedForce(game, effectResult, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place top card of Used Pile on Force Pile");
            action.setActionMsg("Place top card of Used Pile on Force Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            // Perform result(s)
            action.appendEffect(
                    new PlaceTopCardOfUsedPileOnTopOfForcePileEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}
