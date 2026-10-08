package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TransferDeviceOrWeaponEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Blaster Rack
 */
public class Card1_211 extends AbstractNormalEffect {
    public Card1_211() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Blaster_Rack, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("Imperial facilities like the Death Star and garrison bases have blaster racks at key locations to equip soldiers with weapons like blaster rifles and thermal detonators.");
        setGameText("Deploy on your side of table. At any time, you may transfer one of your character weapons from any site to the Blaster Rack. During your deploy phase, weapon may be transferred to your character on table for an expenditure of Force equal to the weapon's deploy cost.");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter filter = Filters.and(Filters.your(playerId), Filters.character_weapon, Filters.at(Filters.site));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Transfer character weapon");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character weapon", filter) {
                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.setActionMsg("Stack " + GameUtils.getCardLink(weapon));
                            // Perform result(s)
                            action.appendEffect(
                                    new StackCardFromTableEffect(action, weapon, self));
                        }
                    });
            actions.add(action);
        }

        final Filter yourCharacters = Filters.and(Filters.your(self), Filters.character);
        Filter transferableStackedWeapon = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.weapon.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                Filter validTarget = Filters.and(yourCharacters,
                        physicalCard.getBlueprint().getValidToUseWeaponFilter(playerId, gameState.getGame(), physicalCard));
                if (!Filters.canSpot(gameState.getGame(), self, validTarget)) {
                    return false;
                }
                float cost = modifiersQuerying.getDeployCost(gameState, physicalCard);
                return GameConditions.canUseForce(gameState.getGame(), playerId, cost);
            }
        };

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasStackedCards(game, self, transferableStackedWeapon)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Transfer stacked weapon");
            // Choose target(s)
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, self, transferableStackedWeapon) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.setActionMsg("Transfer " + GameUtils.getCardLink(selectedCard));
                            Filter validTarget = Filters.and(yourCharacters,
                                    selectedCard.getBlueprint().getValidToUseWeaponFilter(playerId, game, selectedCard));
                            action.appendEffect(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose character to transfer to", validTarget) {
                                        @Override
                                        protected void cardSelected(PhysicalCard character) {
                                            float cost = game.getModifiersQuerying().getDeployCost(game.getGameState(), selectedCard);
                                            if (cost > 0) {
                                                action.appendEffect(new UseForceEffect(action, playerId, cost));
                                            }
                                            action.appendEffect(
                                                    new TransferDeviceOrWeaponEffect(action, selectedCard, character, null));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
