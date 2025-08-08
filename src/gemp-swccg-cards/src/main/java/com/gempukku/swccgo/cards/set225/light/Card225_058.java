package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayersPhaseCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Effect
 * Title: Yoda's Hope (V)
 */
public class Card225_058 extends AbstractNormalEffect {
    public Card225_058() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Yodas_Hope, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("'You must feel the Force around you. Here, between you, me, the tree, the rock, everywhere! Yes, even between the land and the ship.'");
        setGameText("If your [Dagobah] objective on table, deploy on Yoda. Your training destiny draws are +1. Jedi Tests may be attempted at start of opponent's deploy phase (draw two training destiny and choose one). [Immune to Alter.]");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.DAGOBAH, Filters.Objective));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Yoda;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition opponentsDeployPhaseCondition = new PlayersPhaseCondition(opponent, Phase.DEPLOY);

        modifiers.add(new EachTrainingDestinyModifier(self, Filters.any, 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test, ModifyGameTextType.JEDI_TESTS__MAY_ATTEMPT_IN_OPPONENTS_DEPLOY_PHASE));
        modifiers.add(new SpecialFlagModifier(self, opponentsDeployPhaseCondition, ModifierFlag.DRAW_TWO_AND_CHOOSE_ONE_FOR_TRAINING_DESTINY, playerId));
        return modifiers;
    }
}