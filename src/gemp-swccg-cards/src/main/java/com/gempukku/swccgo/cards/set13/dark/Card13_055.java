package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceForcePileOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Alien
 * Title: Bib Fortuna
 */
public class Card13_055 extends AbstractAlien {
    public Card13_055() {
        super(Side.DARK, 3, 2, 3, 1, 3, Title.Bib, Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Twi'lek who serves as Jabba's major-domo. First to discover the profitability of ryll spice found on Ryloth.");
        setGameText("While with Jabba, power +3 and, during your draw phase, may use 1 Force to count the number of cards in your Force Pile, place your Force Pile in your Used Pile, and then activate Force up to the counted number.");
        addPersona(Persona.BIB);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        setSpecies(Species.TWILEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.Jabba), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DRAW)
                && GameConditions.isWith(game, self, Filters.Jabba)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place Force Pile in Used Pile");
            action.setActionMsg("Place Force Pile in Used Pile to activate Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            int forcePileSize = game.getGameState().getForcePileSize(playerId);
                            if (forcePileSize > 0) {
                                action.appendEffect(
                                        new PlaceForcePileOnUsedPileEffect(action, playerId));
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to activate ", 1, forcePileSize, forcePileSize) {
                                                    @Override
                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                        game.getGameState().sendMessage(playerId + " chooses to activate " + result + " Force");
                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new ActivateForceEffect(action, playerId, result));
                                                    }
                                                }
                                        ));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
