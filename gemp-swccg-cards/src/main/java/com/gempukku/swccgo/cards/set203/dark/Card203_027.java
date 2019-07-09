package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Republic
 * Title: General Grievous
 */
public class Card203_027 extends AbstractRepublic {
    public Card203_027() {
        super(Side.DARK, 1, 5, 6, 4, 7, "General Grievous", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Trade Federation.");
        setGameText("Any stolen lightsaber may deploy on Grievous. Once per game, Grievous may steal a lightsaber from opponent's Lost Pile. Unless alone, may lose 1 Force to return Grevious (and your cards on him) to hand from a site you control. Immune to attrition < 4.");
        addKeywords(Keyword.GENERAL);
        addIcons(Icon.EPISODE_I, Icon.SEPERATIST, Icon.VIRTUAL_SET_3);
        addIcon(Icon.WARRIOR, 4);
        addPersona(Persona.GRIEVOUS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter stolenLightsaber = Filters.and(Filters.your(self), Filters.stolen, Filters.lightsaber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, stolenLightsaber, self));
        modifiers.add(new MayUseWeaponModifier(self, stolenLightsaber));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.GENERAL_GRIEVOUS__STEAL_LIGHTSABER_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSearchOpponentsLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal lightsaber from Lost Pile");
            action.setActionMsg("Steal a lightsaber from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.lightsaber));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.isAlone(game, self)
                && GameConditions.isAtLocation(game, self, Filters.and(Filters.site, Filters.controls(playerId)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Return to hand");
            action.setActionMsg("Return " + GameUtils.getCardLink(self) + " to hand");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self, Zone.HAND, Zone.LOST_PILE));
            actions.add(action);
        }
        return actions;
    }
}
