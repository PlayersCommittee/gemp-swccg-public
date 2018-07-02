package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for immunity to specified card titles.
 */
public class ImmuneToTitleModifier extends AbstractModifier {
    private String _immuneToTitle;

    /**
     * Creates a modifier for immunity to cards of a specified title.
     * @param source the card that is the source of the modifier and that is given immunity
     * @param immuneToName the title of card immune to
     */
    public ImmuneToTitleModifier(PhysicalCard source, String immuneToName) {
        this(source, source, null, immuneToName);
    }

    /**
     * Creates a modifier for immunity to cards of a specified title.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards given immunity
     * @param immuneToTitle the title of card immune to
     */
    public ImmuneToTitleModifier(PhysicalCard source, Filterable affectFilter, String immuneToTitle) {
        this(source, affectFilter, null, immuneToTitle);
    }

    /**
     * Creates a modifier for immunity to cards of a specified title.
     * @param source the card that is the source of the modifier and that is given immunity
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param immuneToTitle the title of card immune to
     */
    public ImmuneToTitleModifier(PhysicalCard source, Condition condition, String immuneToTitle) {
        this(source, source, condition, immuneToTitle);
    }

    /**
     * Creates a modifier for immunity to cards of a specified title.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards given immunity
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param immuneToTitle the title of card immune to
     */
    public ImmuneToTitleModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String immuneToTitle) {
        super(source, null, affectFilter, condition, ModifierType.IMMUNE_TO_TITLE, true);
        _immuneToTitle = immuneToTitle;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Immune to " + _immuneToTitle;
    }

    @Override
    public boolean isImmuneToCardModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, SwccgBuiltInCardBlueprint permanentWeapon) {
        return Filters.title(_immuneToTitle).accepts(gameState, modifiersQuerying, card)
                || (permanentWeapon != null && Filters.title(_immuneToTitle).accepts(gameState, modifiersQuerying, permanentWeapon));
    }

    @Override
    public boolean isImmuneToCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle) {
        return cardTitle.equals(_immuneToTitle);
    }
}
