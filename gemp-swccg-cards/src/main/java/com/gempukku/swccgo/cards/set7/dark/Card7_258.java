package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Lost
 * Title: Overwhelmed
 */
public class Card7_258 extends AbstractLostInterrupt {
    public Card7_258() {
        super(Side.DARK, 5, "Overwhelmed");
        setLore("When the Empire amasses its fleet, the only option for the Alliance is retreat.");
        setGameText("During your deploy phase, target a system where your total power is more than double opponent's total power and opponent has no Jedi or starship weapon. Place all opponent's starships there (and cards on them) in owner's Used Pile.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            String opponent = game.getOpponent(playerId);
            final GameState gameState = game.getGameState();
            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            Collection<PhysicalCard> systems = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.sameSystemAs(self, Filters.and(Filters.opponents(self), Filters.starship)),
                    Filters.not(Filters.sameSystemAs(self, Filters.and(Filters.opponents(self), Filters.or(Filters.Jedi, Filters.starship_weapon))))));

            List<PhysicalCard> validSystems = new LinkedList<PhysicalCard>();
            for (PhysicalCard system : systems) {
                float yourTotalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, system, playerId, false, false);
                float opponentsTotalPower = modifiersQuerying.getTotalPowerAtLocation(gameState, system, opponent, false, false);
                if (yourTotalPower > (2 * opponentsTotalPower)) {
                    validSystems.add(system);
                }
            }
            if (!validSystems.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place opponent's starships in Used Pile");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose system", validSystems) {
                            @Override
                            protected void cardSelected(PhysicalCard system) {
                                final Collection<PhysicalCard> starships = Filters.filterActive(game, self, Filters.and(Filters.opponents(self), Filters.starship, Filters.at(system)));
                                action.addAnimationGroup(starships);
                                // Allow response(s)
                                action.allowResponses("Place  " + GameUtils.getAppendedNames(starships) + " in Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlaceCardsInUsedPileFromTableEffect(action, starships, false, Zone.USED_PILE));
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