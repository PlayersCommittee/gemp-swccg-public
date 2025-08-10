package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Ambush
 */
public class Card5_031 extends AbstractLostInterrupt {
    public Card5_031() {
        super(Side.LIGHT, 3, Title.Ambush, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("'Well done. Hold them in the security tower, and keep it quiet. Move.'");
        setGameText("During your turn, target a site where your total power is more than double opponent's total power. Unless opponent has a Dark Jedi or character weapon there, place each opponent character, vehicle and starship there (and cards on them) in owner's Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, self)) {
            String opponent = game.getOpponent(playerId);
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            Collection<PhysicalCard> sites = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship))),
                    Filters.not(Filters.sameSiteAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.Dark_Jedi, Filters.character_weapon_or_character_with_permanent_character_weapon))))));

            List<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
            for (PhysicalCard site : sites) {
                float yourTotalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, site, playerId, false, false);
                float opponentsTotalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, site, opponent, false, false);
                if (yourTotalPower > (2 * opponentsTotalPower)) {
                    validSites.add(site);
                }
            }
            if (!validSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place opponent's cards in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site", validSites) {
                            @Override
                            protected void cardSelected(PhysicalCard site) {
                                final Collection<PhysicalCard> cards = Filters.filterActive(game, self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship), Filters.at(site)));
                                action.addAnimationGroup(cards);
                                // Allow response(s)
                                action.allowResponses("Place  " + GameUtils.getAppendedNames(cards) + " in Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlaceCardsInUsedPileFromTableEffect(action, cards, false, Zone.USED_PILE));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}