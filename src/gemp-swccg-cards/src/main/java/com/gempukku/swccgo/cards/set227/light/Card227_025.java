package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPersonalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Effect
 * Title: The Greatest Of All The Jedi
 */
public class Card227_025 extends AbstractNormalEffect {
    public Card227_025() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Greatest_Of_All_The_Jedi, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setGameText("If your [Skywalker] Epic Event on table, deploy on table. Your personal Force generation = 2. While Jedi Anakin in battle alone, your total battle destiny is +1 for each of opponent's characters there and may place Effect out of play to allow Anakin's weapon to fire twice. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.SKYWALKER, Icon.VIRTUAL_SET_27);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.SKYWALKER, Filters.Epic_Event));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<>();

        // Your personal Force generation = 2.
        modifiers.add(new ResetPersonalForceGenerationModifier(self, 2, playerId));

        // While your Jedi Anakin is in battle alone.
        Condition jediAnakinInBattleAlone = new DuringBattleWithParticipantCondition(
            Filters.and(Filters.your(self), Filters.Anakin, Filters.Jedi, Filters.alone));

        // Your total battle destiny is +1 for each of opponent's characters in the battle.
        modifiers.add(new TotalBattleDestinyModifier(self, jediAnakinInBattleAlone,
            new InBattleEvaluator(self, Filters.and(Filters.opponents(self), Filters.character)), playerId));

        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Jedi, Filters.Anakin, Filters.participatingInBattle, Filters.alone))
            && GameConditions.canSpot(game, self, Filters.and(Filters.Anakin, Filters.armedWith(Filters.weapon)))
            && GameConditions.canBePlacedOutOfPlay(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Fire a weapon twice");
            action.setActionMsg("Place out of place to fire a weapon twice");
            action.appendTargeting(
                new ChooseCardOnTableEffect(action, playerId, "Choose weapon", Filters.and(Filters.your(self), Filters.weapon, Filters.presentInBattle)) {
                    @Override
                    protected void cardSelected(final PhysicalCard weapon) {
                        action.appendCost(
                            new PlaceCardOutOfPlayFromTableEffect(action, self));
                        action.addAnimationGroup(weapon);
                        action.appendEffect(
                            new AddUntilEndOfBattleModifierEffect(action, new MayBeFiredTwicePerBattleModifier(self, weapon), null));
                    }
                }
            );

            actions.add(action);
        }

        return actions;
    }
}
