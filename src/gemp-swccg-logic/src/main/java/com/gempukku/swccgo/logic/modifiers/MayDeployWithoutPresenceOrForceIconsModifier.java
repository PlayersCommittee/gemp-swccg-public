package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier which allows specified cards to deploy without presence or Force icons.
 */
public class MayDeployWithoutPresenceOrForceIconsModifier extends MayDeployToLocationWithoutPresenceOrForceIconsModifier {

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons.
     * @param source the card that is the source of the modifier and that is allowed to deploy without presence or Force icons
     */
    public MayDeployWithoutPresenceOrForceIconsModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     */
    public MayDeployWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Filters.any);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy without presence or Force icons";
    }
}
