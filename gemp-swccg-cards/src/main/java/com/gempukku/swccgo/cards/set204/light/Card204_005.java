package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Alien
 * Title: Crusher Roodown
 */
public class Card204_005 extends AbstractAlien {
    public Card204_005() {
        super(Side.LIGHT, 2, 3, 5, 1, 5, "Crusher Roodown", Uniqueness.UNIQUE);
        setLore("Abednedo scavenger.");
        setGameText("Power -3 with Unkar Plutt. Once per battle, may use 1 Force to peek at cards stacked on Graveyard Of Giants; place one in owner's Lost Pile to add one destiny to total power at same or adjacent site. May take your starship just drawn for destiny into hand.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.SCAVENGER);
        setSpecies(Species.ABEDNEDO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.Unkar_Plutt), -3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.sameOrAdjacentSite(self))
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
            PhysicalCard graveyardOfGiants = Filters.findFirstActive(game, self, Filters.and(Filters.Graveyard_Of_Giants, Filters.hasStacked(Filters.any)));
            if (graveyardOfGiants != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at cards on Graveyard Of Giants");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                action.appendCost(
                        new ChooseStackedCardEffect(action, playerId, graveyardOfGiants) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendCost(
                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, true));
                                // Perform result(s)
                                action.appendEffect(
                                        new AddDestinyToTotalPowerEffect(action, 1));
                            }
                            @Override
                            public String getChoiceText(int numCardsToChoose) {
                                return "Choose card" + GameUtils.s(numCardsToChoose) + " to place in owner's Lost Pile";
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.starship)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take destiny card into hand");
            action.setActionMsg("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand");
            // Perform result(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
