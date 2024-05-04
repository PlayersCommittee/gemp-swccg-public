package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Effect
 * Title: Echo Base Sensors
 */
public class Card13_017 extends AbstractNormalEffect {
    public Card13_017() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Echo Base Sensors", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("'The sensors are in place. You'll know if anything comes around.'");
        setGameText("Deploy on North Ridge if Main Power Generators on table. When opponent deploys a character or vehicle to a marker site, activate 1 Force and that card may not move that turn. Effect lost if opponent occupies an Echo site, or your Ice Storm on table. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.REFLECTIONS_III);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.North_Ridge;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpotFromTopLocationsOnTable(game, Filters.Main_Power_Generators);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.or(Filters.character, Filters.vehicle), Filters.marker_site)) {
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force and prevent " + GameUtils.getFullName(cardDeployed) + " from moving");
            action.setActionMsg("Activate 1 Force and prevent " + GameUtils.getCardLink(cardDeployed) + " from moving until end of turn");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action, new MayNotMoveModifier(self, cardDeployed),
                            "Prevents " + GameUtils.getCardLink(cardDeployed) + " from moving until end of turn"));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && (GameConditions.occupies(game, opponent, Filters.Echo_site)
                || GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Ice_Storm)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}