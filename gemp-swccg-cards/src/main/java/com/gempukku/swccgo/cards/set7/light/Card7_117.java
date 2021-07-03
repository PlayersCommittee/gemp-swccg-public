package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractMobileSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Death Star
 */
public class Card7_117 extends AbstractMobileSystem {
    public Card7_117() {
        super(Side.LIGHT, Title.Death_Star, 1, 0);
        setLocationDarkSideGameText("X = parsec of current position (starts at 0). Immune to Revolution. You may move Death Star (hyperspeed = 1).");
        setLocationLightSideGameText("Deploy if Death Star Plans completed and Death Star system not on table. If 'blown away,' Dark Side loses +8 Force.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return (GameConditions.hasCompletedUtinniEffect(game, playerId, Filters.Death_Star_Plans)
                || GameConditions.hasGameTextModification(game, self, ModifyGameTextType.DEATH_STAR__MAY_DEPLOY_WITHOUT_COMPLETING_DEATH_STAR_PLANS))
                && !GameConditions.canSpotLocation(game, Filters.Death_Star_system);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new AddToBlownAwayForceLossEffect(action, game.getDarkPlayer(), 8));
            return Collections.singletonList(action);
        }
        return null;
    }
}