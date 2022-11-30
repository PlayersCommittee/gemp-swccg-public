package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Officer Perosei
 */
public class Card14_022 extends AbstractRepublic {
    public Card14_022() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, "Officer Perosei", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Royal Naboo security guard who can disassemble and reassemble a Naboo blaster in less than sixty seconds. During the occupation, was a prisoner at Camp Four.");
        setGameText("Once during your deploy phase, may use 1 Force to relocate Perosei to an adjacent Naboo site. While with a Republic character during battle, may place Perosei out of play to add 5 to your total power this battle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.ROYAL_NABOO_SECURITY, Keyword.GUARD);
        addPersona(Persona.PEROSEI);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        if(GameConditions.canSpot(game, self, Filters.Perosei)) {
            final PhysicalCard perosei = Filters.findFirstActive(game, self, Filters.Perosei);
            // Card action 1
            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

            // Check condition(s)
            if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
                Filter siteToRelocateTo = Filters.and(Filters.Naboo_site, Filters.adjacentSite(self), Filters.locationCanBeRelocatedTo(perosei, 1));
                if (GameConditions.canSpotLocation(game, siteToRelocateTo)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Relocate to adjacent site");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, self.getOwner(), "Choose site to relocate " + GameUtils.getCardLink(perosei), siteToRelocateTo) {
                                @Override
                                protected void cardSelected(final PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, self, selectedCard, 1));
                                    // Allow response(s)
                                    action.allowResponses("Relocate " + GameUtils.getCardLink(selectedCard) + " to " + GameUtils.getCardLink(selectedCard),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RelocateBetweenLocationsEffect(action, perosei, selectedCard));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }

            // Card action 2
            gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

            // Check condition(s)
            if (GameConditions.isInBattleWith(game, self, Filters.Republic_character)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place Perosei out of play to add 5 to total power");
                action.setActionMsg("Add 5 to total power");
                // Pay cost(s)
                action.appendCost(
                        new PlaceCardOutOfPlayFromTableEffect(action, perosei));
                // Perform result(s)
                action.appendEffect(
                        new ModifyTotalPowerUntilEndOfBattleEffect(action, 5, playerId, "Adds 5 to total power"));
                actions.add(action);
            }
        }

        return actions;
    }
}
