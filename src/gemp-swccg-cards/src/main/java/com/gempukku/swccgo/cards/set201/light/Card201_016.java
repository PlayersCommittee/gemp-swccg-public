package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Location
 * Subtype: System
 * Title: Anoat (V)
 */
public class Card201_016 extends AbstractSystem {
    public Card201_016() {
        super(Side.LIGHT, Title.Anoat, 5, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Your Ugnaughts deploy free aboard starships here. If you control, all your Ugnaughts on table are forfeit +2.");
        setLocationLightSideGameText("Force loss from Security Precautions is reduced by 4.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.PLANET, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Ugnaught), Filters.and(Filters.starship, Filters.here(self))));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Ugnaught, Filters.onTable),
                new ControlsCondition(playerOnDarkSideOfLocation, self), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, Filters.Security_Precautions)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)) {
            String playerToLoseForce = ((AboutToLoseForceResult) effectResult).getPlayerToLoseForce();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce Force loss by 4");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ReduceForceLossEffect(action, playerToLoseForce, 4));
            return Collections.singletonList(action);
        }
        return null;
    }
}