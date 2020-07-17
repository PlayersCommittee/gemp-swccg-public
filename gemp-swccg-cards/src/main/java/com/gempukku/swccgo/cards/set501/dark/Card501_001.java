package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Ozzel (v)
 */
public class Card501_001 extends AbstractImperial {
    public Card501_001() {
        super(Side.DARK, 0, 2, 2, 2, 4, Title.Ozzel, Uniqueness.UNIQUE);
        setLore("Leader of the Emperor's Death Squadron assigned to hunt down and crush the Rebellion. As clumsy as he is stupid. Has just failed Darth Vader for the next-to-last time.");
        setGameText("Adds 2 to the power of anything he pilots. Matching pilot for any death squadron star destroyer, and, while piloting one, instead of force draining may draw top card of Reserve Deck or raise your same or related location to the top.");
        addIcons(Icon.HOTH, Icon.PILOT, Icon.VIRTUAL_SET_13);
        addPersona(Persona.OZZEL);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.and(Filters.Star_Destroyer, Filters.loreContains("Death Squadron")));
        setVirtualSuffix(true);
        setTestingText("Admiral Ozzel (v)");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isPiloting(game, self, Filters.and(Filters.Star_Destroyer, Filters.loreContains("Death Squadron")))) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            final PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, self);
            if (GameConditions.canInsteadOfForceDrainingAtLocation(game, playerId, location)) {
                if (GameConditions.hasReserveDeck(game, playerId)) {
                    TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Draw top card Of Reserve Deck");
                    action.setActionMsg("Draw top card Of Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(
                            new InsteadOfForceDrainingEffect(action, location,
                                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId)));
                    actions.add(action);

                }
                Filter sameOrRelatedLocation = Filters.and(Filters.canBeConvertedByRaisingYourLocationToTop(playerId), Filters.sameOrRelatedLocation(self));
                if (GameConditions.canTarget(game, self, sameOrRelatedLocation)) {
                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Raise location to the top");
                    action.setActionMsg("Raise location to the top");
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose location to convert", sameOrRelatedLocation) {
                                @Override
                                protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Pay cost(s)
                                    // Allow response(s)
                                    action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(new InsteadOfForceDrainingEffect(action, location,
                                                            new ConvertLocationByRaisingToTopEffect(action, targetedCard, true)));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
        }
        return actions;
    }
}
