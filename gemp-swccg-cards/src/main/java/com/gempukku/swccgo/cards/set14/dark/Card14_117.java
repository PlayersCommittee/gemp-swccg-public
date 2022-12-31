package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: DFS-1308
 */
public class Card14_117 extends AbstractStarfighter {
    public Card14_117() {
        super(Side.DARK, 2, 3, 2, null, 2, null, 3, "DFS-1308", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("1308 was one of several droids who analyzed the N-1 starfighter during the Trade Federation's occupation of Naboo. Was unable to calculate the 'human factor' inside the N-1.");
        setGameText("Deploys -1 to same location as your battleship. While with another droid starfighter at a system, opponent may not draw more than one battle destiny here.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.DFS_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.battleship))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new AndCondition(new AtCondition(self, Filters.system),
                new WithCondition(self, Filters.and(Filters.other(self), Filters.droid_starfighter))), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
