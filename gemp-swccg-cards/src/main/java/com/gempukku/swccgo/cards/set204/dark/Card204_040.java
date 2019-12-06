package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MinLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: First Order
 * Title: First Order Stormtrooper
 */
public class Card204_040 extends AbstractFirstOrder {
    public Card204_040() {
        super(Side.DARK, 3, 2, 2, 1, 3, "First Order Stormtrooper");
        setGameText("Deploys free to same site as a First Order leader. Opponent's characters here are cumulatively defense value -1 (limit -3).");
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.STORMTROOPER);
        ;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.First_Order_leader)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)),
                new MinLimitEvaluator(new NegativeEvaluator(new HereEvaluator(self, Filters.and(Filters.your(self), Filters.sameTitle(self), Filters.not(Filters.isGameTextCanceled)))), -3)));
        return modifiers;
    }
}
