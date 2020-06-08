package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Starship
 * Subtype: Starfighter
 * Title: The Falcon, Junkyard Garbage
 */
public class Card204_035 extends AbstractStarfighter {
    public Card204_035() {
        super(Side.LIGHT, 0, 0, 3, null, 4, 6, 7, "The Falcon, Junkyard Garbage", Uniqueness.UNIQUE);
        setFrontOfDoubleSidedCard(true);
        setLore("The Millennium Falcon's well-known reputation is favorable not only for its captain and first mate, but for the Alliance as well.");
        setGameText("May not be placed in Reserve Deck. If Falcon about to leave table, place it out of play. May add 2 pilots and 2 passengers. Has ship-docking capability. While [Episode VII] Han or Rey piloting, maneuver +2 and immune to attrition < 4 (< 6 if both). Once during your move phase, if at a site, may flip this card (even if unpiloted).");
        addPersona(Persona.FALCON);
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.RESISTANCE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Han, Filters.Rey));
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");


            // Perform result(s)

            // If overwhelmed, put all cards aboard in Used pile before self-destructing!
            if (!TriggerConditions.isAboutToLeaveTableExceptFromSourceCard(game, effectResult, self, Filters.Overwhelmed)) {
                Collection<PhysicalCard> cardsAboard = Filters.filterActive(game, self, Filters.aboard(self));
                action.appendEffect(new PlaceCardsInUsedPileFromTableEffect(action, cardsAboard, false, Zone.USED_PILE, true));
            }

            // In all other cases (not Overwhelmed), just go out of play like normal
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                            }
                        }
                    });
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition hanPiloting = new HasPilotingCondition(self, Filters.and(Filters.Han, Icon.EPISODE_VII));
        Condition reyPiloting = new HasPilotingCondition(self, Filters.Rey);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, new OrCondition(hanPiloting, reyPiloting), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(hanPiloting, reyPiloting), new ConditionEvaluator(4, 6, new AndCondition(hanPiloting, reyPiloting))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canBeFlipped(game, self)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Flip");
            action.setActionMsg("Flip " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
