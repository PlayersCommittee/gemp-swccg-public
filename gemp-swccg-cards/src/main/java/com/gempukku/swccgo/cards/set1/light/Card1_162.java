package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAutomatedWeapon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsToLoseFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Weapon
 * Subtype: Automated
 * Title: Timer Mine
 */
public class Card1_162 extends AbstractAutomatedWeapon {
    public Card1_162() {
        super(Side.LIGHT, 2, PlayCardZoneOption.OPPONENTS_SIDE_OF_LOCATION, "Timer Mine");
        setLore("A timer-activated explosive device designed to be placed by a mining droid. Typically used in ore and spice mines for demolition. Also has many military applications.");
        setGameText("Deploy on opponent's side at same site as one of your mining droids. 'Explodes' at beginning of your next turn. Draw destiny. That number of opponent's characters there are immediately lost (owner's choice). Timer Mine is also lost.");
        addKeywords(Keyword.MINE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.mining_droid));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Explode'");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " 'explode'");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();

                            gameState.sendMessage("Destiny: " + (totalDestiny != null ? GuiUtils.formatAsString(totalDestiny) : "Failed destiny draw"));
                            if (totalDestiny != null) {
                                int numCharacters = (int) Math.floor(totalDestiny);
                                if (numCharacters > 0) {
                                    Collection<PhysicalCard> characters = Filters.filterAllOnTable(game, Filters.and(Filters.opponents(self), Filters.character, Filters.present(self)));
                                    if (!characters.isEmpty()) {
                                        numCharacters = Math.min(characters.size(), numCharacters);
                                        action.appendEffect(
                                                new ChooseCardsToLoseFromTableEffect(action, opponent, numCharacters, numCharacters, true, Filters.in(characters)));
                                    }
                                }
                            }
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, self, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
