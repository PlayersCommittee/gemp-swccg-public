package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Admiral's Order
 * Title: Combined Fleet Action
 */
public class Card9_002 extends AbstractAdmiralsOrder {
    public Card9_002() {
        super(Side.LIGHT, "Combined Fleet Action", ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("At each system where any player does not have both a starfighter and a capital starship present, that player's starships there are power -2. At sites related to systems you occupy, during each battle opponent may draw no more than one battle destiny. Once during each of your deploy phases, you may deploy one combat vehicle from your Reserve Deck; reshuffle.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter playersStarshipsAtSystemsWithoutBothStarfighterAndCapital = Filters.and(Filters.owner(playerId), Filters.starship,
                Filters.at(Filters.and(Filters.system, Filters.not(Filters.and(
                        Filters.wherePresent(self, Filters.and(Filters.owner(playerId), Filters.starfighter)),
                        Filters.wherePresent(self, Filters.and(Filters.owner(playerId), Filters.capital_starship)))))));
        Filter opponentsStarshipsAtSystemsWithoutBothStarfighterAndCapital = Filters.and(Filters.owner(opponent), Filters.starship,
                Filters.at(Filters.and(Filters.system, Filters.not(Filters.and(
                        Filters.wherePresent(self, Filters.and(Filters.owner(opponent), Filters.starfighter)),
                        Filters.wherePresent(self, Filters.and(Filters.owner(opponent), Filters.capital_starship)))))));
        Filter sitesRelatedToSystemsYouOccupy = Filters.and(Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, playersStarshipsAtSystemsWithoutBothStarfighterAndCapital, -2));
        modifiers.add(new PowerModifier(self, opponentsStarshipsAtSystemsWithoutBothStarfighterAndCapital, -2));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, sitesRelatedToSystemsYouOccupy, 1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMBINED_FLEET_ACTION__DOWNLOAD_COMBAT_VEHICLE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy combat vehicle from Reserve Deck");
            action.setActionMsg("Deploy a combat vehicle from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.combat_vehicle, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
