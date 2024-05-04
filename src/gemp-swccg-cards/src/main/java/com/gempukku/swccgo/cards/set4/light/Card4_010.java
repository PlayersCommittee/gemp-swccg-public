package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Han's Toolkit
 */
public class Card4_010 extends AbstractDevice {
    public Card4_010() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Hans_Toolkit, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Experienced pilots keep a well equipped toolkit. Horizontal boosters, alluvial dampers and hydrospanners aid in performing high-tech repairs aboard starships.");
        setGameText("Use 1 Force to deploy on one of you characters, vehicles or starships (free on Falcon or Han). While aboard a vehicle or starship, you may use 1 Force to cancel any Interrupt or Effect of any kind which targets that vehicle or starship.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.not(Filters.or(Filters.Falcon, Filters.Han))));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.or(Filters.Falcon, Filters.Han)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.character, Filters.vehicle, Filters.starship);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.character, Filters.vehicle, Filters.starship);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        Filter starshipOrVehicle = Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.or(Filters.hasAttached(self), Filters.hasAboard(self)));
        Filter filter = Filters.and(Filters.or(Filters.Interrupt, Filters.and(Filters.Effect_of_any_Kind, Filters.not(Filters.or(Filters.immune_to_Alter, Filters.immune_to_Control)))),
                Filters.cardBeingPlayedTargeting(self, starshipOrVehicle));

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canUseDevice(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect, 1);
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter starshipOrVehicle = Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.or(Filters.hasAttached(self), Filters.hasAboard(self)));
        Filter filter = Filters.and(Filters.Effect_of_any_Kind, Filters.not(Filters.or(Filters.immune_to_Alter, Filters.immune_to_Control)), Filters.cardOnTableTargeting(starshipOrVehicle));

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canTargetToCancel(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, filter, "Effect of any kind", 1);
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}