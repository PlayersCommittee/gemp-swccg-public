package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Close Air Support
 */
public class Card9_032 extends AbstractNormalEffect {
    public Card9_032() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Close Air Support", Uniqueness.UNIQUE);
        setLore("Attack plans for General Madine's commandos often require coordinated air support. A number of Z-95 headhunters have been modified to operate in this role.");
        setGameText("Deploy on table. While your Z-95 occupies a system or cloud sector, once per turn your scout at a related exterior site may add one battle destiny (if Z-95 is Tala 1 or Tala 2, may also add one destiny to total power only).");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleAt(game, Filters.exterior_site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.scout))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            Filter z95Filter = Filters.and(Filters.your(self), Filters.Z_95, Filters.piloted, Filters.at(Filters.and(Filters.or(Filters.system, Filters.cloud_sector),
                    Filters.relatedLocationTo(self, Filters.battleLocation), Filters.occupies(playerId))));
            if (GameConditions.canSpot(game, self, z95Filter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add one battle destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddBattleDestinyEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                Filter talaFilter = Filters.and(Filters.your(self), Filters.Z_95, Filters.or(Filters.Tala_1, Filters.Tala_2),
                        Filters.piloted, Filters.at(Filters.and(Filters.or(Filters.system, Filters.cloud_sector),
                                Filters.relatedLocationTo(self, Filters.battleLocation), Filters.occupies(playerId))));
                if (GameConditions.canSpot(game, self, talaFilter)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Add one battle destiny and one destiny to power");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new AddBattleDestinyEffect(action, 1));
                    action.appendEffect(
                            new AddDestinyToTotalPowerEffect(action, 1));
                    actions.add(action);
                }
            }
        }
        return actions;
    }
}