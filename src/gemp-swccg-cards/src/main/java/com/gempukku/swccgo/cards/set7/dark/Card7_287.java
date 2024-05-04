package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Rendili
 */
public class Card7_287 extends AbstractSystem {
    public Card7_287() {
        super(Side.DARK, Title.Rendili, 2, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLocationDarkSideGameText("If you control, once during each of your control phases, may use 3 Force to retrieve a Victory-class Star Destroyer.");
        setLocationLightSideGameText("Force drain -1 here. If you occupy, opponent's Rendili game text is canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerOnDarkSideOfLocation, 3)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Retrieve a Victory-class Star Destroyer");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerOnDarkSideOfLocation, 3));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerOnDarkSideOfLocation, Filters.Victory_class_Star_Destroyer));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Rendili_system,
                new OccupiesCondition(playerOnLightSideOfLocation, self), game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}