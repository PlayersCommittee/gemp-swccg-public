package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
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
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.DoubleTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: I'm With You Too
 */
public class Card9_035 extends AbstractNormalEffect {
    public Card9_035() {
        super(Side.LIGHT, 7, PlayCardZoneOption.ATTACHED, Title.Im_With_You_Too, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Luke completes the legendary foursome.");
        setGameText("Deploy on Luke if That's One and Count Me In are on table. When Han, Chewie, Leia and Luke are involved in the same battle, you may double your total battle destiny and Han, Chewie, Leia, and Luke are immune to attrition. Don't Get Cocky is a Used Interrupt.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Luke;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Luke;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Thats_One) && GameConditions.canSpot(game, self, Filters.Count_Me_In);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.or(Filters.Han, Filters.Chewie, Filters.Leia, Filters.Luke),
                new AndCondition(new DuringBattleWithParticipantCondition(Filters.Han), new DuringBattleWithParticipantCondition(Filters.Chewie),
                        new DuringBattleWithParticipantCondition(Filters.Leia), new DuringBattleWithParticipantCondition(Filters.Luke))));
        modifiers.add(new UsedInterruptModifier(self, Filters.Dont_Get_Cocky));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Han)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Chewie)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Leia)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Double total battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DoubleTotalBattleDestinyEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}