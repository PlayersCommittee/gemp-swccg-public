package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * SubType: Alien
 * Title: Qi'ra
 */
public class Card501_067 extends AbstractAlien {
    public Card501_067() {
        super(Side.LIGHT, 2, 3, 3, 4, 5, "Qi'ra", Uniqueness.UNIQUE);
        setLore("Female thief. Correlian");
        setGameText("Qi’ra’s deploy cost may not be increased. May use any ‘stolen’ weapon. Once per game, Qi’ra may steal a non-lightsaber character weapon (or device) into hand from opponent’s Lost pile. While Han on table, Qi’ra and Han are each defense value = 5 and immune to attrition < 5");
        addPersona(Persona.QIRA);
        setSpecies(Species.CORELLIAN);
        addKeywords(Keyword.FEMALE, Keyword.THIEF);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        setTestingText("Qi'ra");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveDeployCostIncreasedModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition hanOnTableCondition = new OnTableCondition(self, Filters.Han);
        Filter hanAndQiraFilter = Filters.or(Filters.Han, self);
        modifiers.add(new MayNotHaveDeployCostIncreasedModifier(self));
        modifiers.add(new MayUseWeaponModifier(self, Filters.stolen));
        modifiers.add(new ResetDefenseValueModifier(self, hanAndQiraFilter, hanOnTableCondition, 5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, hanAndQiraFilter, hanOnTableCondition, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QIRA__STEAL_WEAPON_OR_DEVICE_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal card from Lost Pile");
            action.setActionMsg("Steal a card from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.or(Filters.device, Filters.and(Filters.character_weapon, Filters.not(Filters.lightsaber)))));
            return Collections.singletonList(action);
        }
        return null;
    }
}
