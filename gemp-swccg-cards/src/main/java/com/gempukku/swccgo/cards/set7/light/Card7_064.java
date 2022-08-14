package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DoubledCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Hoth Sentry
 */
public class Card7_064 extends AbstractNormalEffect {
    public Card7_064() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Hoth_Sentry, Uniqueness.UNIQUE);
        setLore("'Reroute all power to the energy shield. We've got to hold them till all transports are away.'");
        setGameText("Use 2 Force to deploy at any Hoth site. Declare one of the following to affect that site and adjacent sites while 'sentry' present: Opponent's deploy +1. OR Opponent's ability required for battle destiny +1. OR Your total power +1.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy to make deploy +1"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy to make ability required +1"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_3, PlayCardZoneOption.ATTACHED, "Deploy to make total power +1"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Hoth_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Condition playCardOptionId2 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2);
        Condition playCardOptionId3 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_3);
        Evaluator evaluator = new ConditionEvaluator(1, 2, new DoubledCondition(self));
        Condition appliesAllModifiers = new GameTextModificationCondition(self, ModifyGameTextType.HOTH_SENTRY__APPLIES_ALL_MODIFIERS);
        Filter sameAndAdjacentSite = Filters.sameOrAdjacentSite(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.opponents(self),
                new OrCondition(playCardOptionId1, appliesAllModifiers), evaluator, sameAndAdjacentSite));
        modifiers.add(new IncreaseAbilityRequiredForBattleDestinyModifier(self, sameAndAdjacentSite,
                new OrCondition(playCardOptionId2, appliesAllModifiers), evaluator, opponent));
        modifiers.add(new TotalPowerModifier(self, sameAndAdjacentSite,
                new OrCondition(playCardOptionId3, appliesAllModifiers), evaluator, playerId));
        return modifiers;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        String text = "Chosen effect is: ";
        if (self.getPlayCardOptionId() == PlayCardOptionId.PLAY_CARD_OPTION_1) {
            return text + "Opponent's deploy +1";
        }
        if (self.getPlayCardOptionId() == PlayCardOptionId.PLAY_CARD_OPTION_2) {
            return text + "Opponent's ability required for battle destiny +1";
        }
        if (self.getPlayCardOptionId() == PlayCardOptionId.PLAY_CARD_OPTION_3) {
            return text + "Your total power +1";
        }
        return null;
    }
}