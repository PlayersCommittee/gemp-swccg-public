package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: A Brave Resistance
 */
public class Card501_026 extends AbstractNormalEffect {
    public Card501_026() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Brave Resistance", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If Jakku on table, deploy on table. Your Force generation is +1 at Jakku battlegrounds you occupy. During your deploy phase, may place a Resistance character from hand on top of Used Pile to [upload] a Resistance character of equal or lesser ability. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("A Brave Resistance (ERRATA)");
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpotLocation(game, Filters.Jakku_system);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.Jakku_battleground, Filters.occupies(playerId)), 1, playerId));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.A_BRAVE_RESISTANCE__UPLOAD_RESISTANCE_CHARACTER;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, Filters.and(Filters.Resistance_character, Filters.canBeTargetedBy(self)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Resistance character from hand on Used Pile");
            action.setActionMsg("[upload] a Resistance character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, Filters.Resistance_character) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new PutCardFromHandOnUsedPileEffect(action, playerId, selectedCard, false));
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Resistance_character, Filters.abilityLessThanOrEqualTo(selectedCard.getBlueprint().getAbility())), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}