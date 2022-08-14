package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Character
 * Subtype: Alien
 * Title: Garindan (V)
 */
public class Card601_083 extends AbstractAlien {
    public Card601_083() {
        super(Side.DARK, 4, 3, 1, 2, 3, "Garindan", Uniqueness.UNIQUE);
        setLore("Long-nosed, male Kubaz from Kubindi. Spy. Squealed on Obi-Wan and Luke outside Docking Bay 94. Works for Jabba the Hutt or the highest bidder. Not particularly brave.");
        setGameText("Adds one to the power of anything he pilots. May deploy -1 as a 'react'; shuffle any Reserve Deck. Ignores Objective deployment restrictions. Unless alone, may place Garindan in Used Pile to cancel a just drawn weapon destiny here. [Virtual Block 2] Imperial Domination ignores Garindan.");
        addKeywords(Keyword.SPY);
        addIcons(Icon.PILOT, Icon.LEGACY_BLOCK_7);
        setSpecies(Species.KUBAZ);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresDeploymentRestrictionsFromCardModifier(self, self, null, self.getOwner(), Filters.Objective));
        modifiers.add(new MayDeployAsReactModifier(self, -1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.justDeployed(game, effectResult, self)
                && TriggerConditions.reactedToLocation(game, effectResult, self, Filters.here(self))
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {
            //TODO should really check to make sure his text was used to react

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Shuffle any Reserve Deck");
            action.appendEffect(new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                @Override
                protected void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                    action.appendEffect(new ShuffleReserveDeckEffect(action, cardPileOwner));
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.LEGACY_BLOCK_2, Filters.title("Imperial Domination")), ModifyGameTextType.LEGACY__IMPERIAL_DOMINATION_IGNORES_GARINDAN));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Unless alone, may place Garindan in Used Pile to cancel a just drawn weapon destiny here.

        // Check condition(s)
        if (!GameConditions.isAlone(game, self)
                && TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.here(self))
                && GameConditions.canCancelDestiny(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel just drawn weapon destiny");
            // Perform result(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
