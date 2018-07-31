package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: Relatively Unprotected
 */
public class Card209_017 extends AbstractNormalEffect {
    public Card209_017() {
        super(Side.LIGHT, 7, null, "Relatively Unprotected", Uniqueness.UNIQUE);
        setLore("'With the Imperial Fleet spread throughout the galaxy in a vain effort to engage us, it is relatively unprotected.'");
        setGameText("Deploy on table. While you occupy two battlegrounds (or opponent's site), you lose no more than 1 Force to Carbon-Freezing or That Thing's Operational. [Immune to Alter]. OR Deploy on a Star Destroyer or vehicle; its immunity to attrition is -4 and it may not 'react.'");
        addIcons(Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deploy on your side of table"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on Star Destroyer or vehicle"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2 ? Filters.or(Filters.Star_Destroyer, Filters.vehicle) : Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new LimitForceLossFromCardModifier(self, Filters.or(Filters.That_Things_Operational, Filters.Carbon_Freezing),
                new AndCondition(new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1),
                        (new OrCondition(new OccupiesCondition(playerId, 2, Filters.battleground),
                                new OccupiesCondition(playerId, Filters.and(Filters.opponents(self), Filters.site)))))
                , 1, playerId));

        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.hasAttached(self), new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2), -4));
        modifiers.add(new MayNotReactModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1), Title.Alter));
        return modifiers;
    }
}
