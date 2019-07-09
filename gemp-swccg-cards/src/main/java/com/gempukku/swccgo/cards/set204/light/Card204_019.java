package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Lost
 * Title: I'm Getting Pretty Good At This
 */
public class Card204_019 extends AbstractLostInterrupt {
    public Card204_019() {
        super(Side.LIGHT, 5, "I'm Getting Pretty Good At This", Uniqueness.UNIQUE);
        setLore("Starship blaster, often slung in turret mounts to take advantage of light weight and quick targeting motions. Installed on many starships including the Millennium Falcon.");
        setGameText("If Finn and either Poe or Rey are in battle together, you may add two battle destiny. OR [Download] Finn aboard any starship or vehicle.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Finn)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Poe, Filters.Rey))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add two battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 2));
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.IM_GETTING_KIND_OF_GOOD_AT_THIS__DOWNLOAD_FINN;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Finn)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy Finn from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Finn aboard any starship or vehicle from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardAboardFromReserveDeckEffect(action, Filters.Finn, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}