package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ActivatedForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Device
 * Title: Hydroponics Station
 */
public class Card1_037 extends AbstractDevice {
    public Card1_037() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Hydroponics_Station);
        setLore("Grows fruits and vegetables. Very efficient water use. Often underground. Feeds moisture farm families, but excess vegetables are often sold at markets.");
        setGameText("Use 1 Force to deploy on any exterior Tatooine site. Cannot be moved. The first Force you activate during your activate phase may be drawn into hand instead. If a Vaporator on table, the second Force you activate may also be drawn into hand.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_Tatooine_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveModifier(self));
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.DO_NOT_SKIP_LISTENER_UPDATES_DURING_FORCE_ACTIVATION, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceActivated(game, effectResult, playerId)
                && GameConditions.canUseDevice(game, self, self)
                && GameConditions.isPhaseForPlayer(game, Phase.ACTIVATE, playerId)
                && (GameConditions.forceActivatedThisPhase(game, playerId) == 1
                    || (GameConditions.forceActivatedThisPhase(game, playerId) == 2)
                    && GameConditions.canSpot(game, self, Filters.Vaporator))) {
            PhysicalCard cardActivated = ((ActivatedForceResult) effectResult).getCard();
            if (cardActivated.getZone() == Zone.TOP_OF_FORCE_PILE) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw activated Force into hand");
                // Update usage limit(s)
                action.appendUsage(
                        new UseDeviceEffect(action, self, self));
                // Perform result(s)
                action.appendEffect(
                        new DrawCardIntoHandFromForcePileEffect(action, playerId));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}