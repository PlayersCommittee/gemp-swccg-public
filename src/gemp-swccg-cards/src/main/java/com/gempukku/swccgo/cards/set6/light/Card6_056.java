package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Projection Of A Skywalker
 */
public class Card6_056 extends AbstractNormalEffect {
    public Card6_056() {
        super(Side.LIGHT, 5, null, Title.Projection_Of_A_Skywalker, Uniqueness.RESTRICTED_2, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("'Greetings, Exalted One. Allow me to introduce myself. I am Luke Skywalker, Jedi Knight and friend to Captain Solo.' Hologram.");
        setGameText("Deploy on your side of table. Your Force drains are +1 at holosites and opponent must lose an additional 1 Force to draw a card with Shot In The Dark. (Immune to Alter.) OR Deploy on any planet site. Opponent's Force drains are -1 here.");
        addIcons(Icon.JABBAS_PALACE);
        addKeyword(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deploy on your side of table"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on any planet site"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2 ? Filters.planet_site : Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.holosite, new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1), 1, playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.opponents(playerId), Filters.Shot_In_The_Dark),
                new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1), ModifyGameTextType.SHOT_IN_THE_DARK__LOSE_ADDITIONAL_FORCE_TO_DRAW));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1), Title.Alter));
        return modifiers;
    }
}