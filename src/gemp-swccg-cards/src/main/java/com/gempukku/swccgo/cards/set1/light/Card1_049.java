package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Ellorrs Madak
 */
public class Card1_049 extends AbstractNormalEffect {
    public Card1_049() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Ellorrs_Madak, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Like many Duros, Madak has natural piloting and navigation skill. Former scout. Freelance instructor. Makes runs to important trade worlds Celanon, Byblos and Yaga Minor.");
        setGameText("Deploy on your non-pilot character (except droids) to give that character [Pilot] skill. Adds 2 to power of anything that character pilots. OR Deploy on your pilot. Adds 1 to power of anything that character pilots. (Immune to Alter.)");
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
        Evaluator evaluator = new ConditionEvaluator(0, 2, new GameTextModificationCondition(self, ModifyGameTextType.ELLORRS_MADAK__ADDITIONAL_2_TO_POWER_BONUS));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, attachedTo, playCardOptionId1, Icon.PILOT));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(attachedTo), playCardOptionId1, new AddEvaluator(2, evaluator)));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(attachedTo), playCardOptionId2, new AddEvaluator(1, evaluator)));
        return modifiers;
    }
}