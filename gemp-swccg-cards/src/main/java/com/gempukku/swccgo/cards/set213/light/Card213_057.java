package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: System
 * Title: Kessel (V)
 */
public class Card213_057 extends AbstractSystem {
    public Card213_057() {
        super(Side.LIGHT, Title.Kessel, 8);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("Force drain +1 here. For remainder of game, Kessel Run is immune to Alter and may not be canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnLightSideOfLocation, self), 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
            self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
            // Add modifier here without creating an action
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new MayNotBeCanceledModifier(self, Filters.Kessel_Run)
            );
            game.getModifiersEnvironment().addUntilEndOfGameModifier(
                    new ImmuneToTitleModifier(self, Filters.Kessel_Run, Title.Alter)
            );
        }
        return null;
    }
}