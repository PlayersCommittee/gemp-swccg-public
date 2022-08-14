package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Baniss Keeg
 */
public class Card1_209 extends AbstractNormalEffect {
    public Card1_209() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Baniss Keeg");
        setLore("Duros are famous spacers and starship engineers. Many are forced to work for the Empire. Some, like Baniss Keeg, train pilots for deep space missions.");
        setGameText("Deploy on your non-pilot character (except droids) to give that character [Pilot] skill.  Adds 2 to power of anything that character pilots. OR Deploy on your pilot. Adds 1 to power of anything that character pilots. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on non-pilot character"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on pilot"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.or(Filters.pilot, Filters.droid)));
        }
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2) {
            return Filters.and(Filters.your(self), Filters.pilot);
        }
        return Filters.none;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        PhysicalCard attachedTo = self.getAttachedTo();
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Condition playCardOptionId2 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, attachedTo, playCardOptionId1, Icon.PILOT));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(attachedTo), playCardOptionId1, 2));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(attachedTo), playCardOptionId2, 1));
        return modifiers;
    }
}