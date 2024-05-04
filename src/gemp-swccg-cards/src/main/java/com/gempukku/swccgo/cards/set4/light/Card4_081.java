package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpecialRule;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Subtype: Sector
 * Title: Asteroid Field
 */
public class Card4_081 extends AbstractSector {
    public Card4_081() {
        super(Side.LIGHT, Title.Asteroid_Field, Uniqueness.DIAMOND_3, ExpansionSet.DAGOBAH, Rarity.C);
        setLocationDarkSideGameText("'Asteroid Rules' in effect here.");
        setLocationLightSideGameText("'Asteroid Rules' in effect here. If you control, may cancel Force drain at related system.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.DAGOBAH, Icon.SPACE);
        addKeywords(Keyword.ASTEROID);
        addSpecialRulesInEffectHere(SpecialRule.ASTEROID_RULES);
        addMayNotBePartOfSystem(Title.Ahch_To);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.relatedSystem(self))
                && GameConditions.canCancelForceDrain(game, playerOnLightSideOfLocation, self)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}