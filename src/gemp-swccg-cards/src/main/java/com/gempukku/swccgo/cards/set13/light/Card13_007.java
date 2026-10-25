package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: Armament Dismantled
 */
public class Card13_007 extends AbstractNormalEffect {
    public Card13_007() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Armament Dismantled", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Divide and conquer.");
        setGameText("Use 4 Force (or 1 Force if Obi-Wan is armed with a lightsaber) to deploy on table if Maul present with Obi-Wan. (Obi-Wan may not move that turn.) Maul's lightsaber may add only 1 to Force drains, and may be 'swung' only once per battle. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Use 4 Force, or 1 Force if Obi-Wan is armed with a lightsaber
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.and(Filters.ObiWan, Filters.armedWith(Filters.lightsaber))), -3));
        return modifiers;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.Maul, Filters.presentWith(self, Filters.ObiWan)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Obi-Wan may not move that turn
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canSpot(game, self, Filters.ObiWan)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Prevent Obi-Wan from moving");
            action.setActionMsg("Prevent Obi-Wan from moving until end of turn");
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotMoveModifier(self, Filters.ObiWan),
                            "Prevents Obi-Wan from moving until end of turn"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
