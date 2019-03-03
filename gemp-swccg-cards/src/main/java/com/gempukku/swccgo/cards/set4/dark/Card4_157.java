package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Location
 * Subtype: Site
 * Title: Big One: Asteroid Cave Or Space Slug Belly
 */
public class Card4_157 extends AbstractSite {
    public Card4_157() {
        super(Side.DARK, Title.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, null, Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("'Cave Rules' in effect here. If you control, may cancel Force drains at system Related to Big One.");
        setLocationLightSideGameText("'Cave Rules' in effect here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.CREATURE_SITE, Icon.PLANET);
        addSpecialRulesInEffectHere(SpecialRule.CAVE_RULES);
        addMayNotBePartOfSystem(Title.Ahch_To);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.relatedSystemTo(self, Filters.relatedBigOne(self)))
                && GameConditions.canCancelForceDrain(game, playerOnDarkSideOfLocation, self)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}