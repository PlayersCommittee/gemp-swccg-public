package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Battle Of Christophsis
 */
public class Card221_050 extends AbstractNormalEffect {
    public Card221_050() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Battle Of Christophsis", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("If a Christophsis location on table, deploy on table. Once per turn, if you just deployed a clone to Christophsis, may draw top card of Used Pile. Once per turn, if your clone in battle with a Jedi or Padawan, may lose 1 Force to add one destiny to total power. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Christophsis_location);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.clone, Filters.with(self, Filters.or(Filters.Jedi, Filters.padawan))))
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1, playerId));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId,  Filters.clone, Filters.Christophsis_location)
                && GameConditions.hasUsedPile(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Used Pile");

            action.appendUsage(
                    new OncePerTurnEffect(action));

            action.appendEffect(
                    new DrawCardsIntoHandFromUsedPileEffect(action, playerId, 1));

            return Collections.singletonList(action);
        }

        return null;
    }
}