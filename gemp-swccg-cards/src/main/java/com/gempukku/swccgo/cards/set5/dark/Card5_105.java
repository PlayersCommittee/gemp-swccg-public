package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.EachCarbonFreezingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Ugnaught
 */
public class Card5_105 extends AbstractAlien {
    public Card5_105() {
        super(Side.DARK, 3, 2, 1, 1, 2, "Ugnaught");
        setLore("Cheap manual labor from Gentes in the Anoat system. Make up Cloud City's second largest population. Responsible for maintenance and menial chores throughout the city.");
        setGameText("When at Carbonite Chamber, cumulatively adds 1 to each Carbon-Freezing destiny draw. During your control phase, may lose 1 Force from hand to search your Lost Pile and take one weapon or device into hand.");
        addIcons(Icon.CLOUD_CITY);
        setSpecies(Species.UGNAUGHT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachCarbonFreezingDestinyModifier(self, new AtCondition(self, Filters.Carbonite_Chamber),
                new ConditionEvaluator(1, 2, new GameTextModificationCondition(self,
                        ModifyGameTextType.UGNAUGHT__DOUBLE_CARBON_FREEZING_DESTINY_BONUS)), true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.UGNAUGHT__UPLOAD_WEAPON_OR_DEVICE_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Lost Pile");
            action.setActionMsg("Take a weapon or device into hand from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceFromHandEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.or(Filters.weapon, Filters.device), false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
