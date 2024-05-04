package com.gempukku.swccgo.cards.set220.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.RemoveKeywordModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Character
 * SubType: Imperial
 * Title: Third Sister
 */
public class Card220_004 extends AbstractImperial {
    public Card220_004() {
        super(Side.DARK, 3, 3, 4, 4, 5, "Third Sister", Uniqueness.UNIQUE, ExpansionSet.SET_20, Rarity.V);
        setLore("Female Inquisitor.");
        setGameText("Deploys +3 to your location. Obi-Wan is not a general. Once per turn, if opponent's card just moved from here, opponent loses 1 Force. Your characters here move (using landspeed) to sites opponent occupies for free. Immune to attrition < 4.");
        addKeywords(Keyword.INQUISITOR, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_20);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, 3, Filters.and(Filters.your(self), Filters.location)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new RemoveKeywordModifier(self, Filters.ObiWan, Keyword.GENERAL));
        modifiers.add(new MovesFreeToLocationUsingLandspeedModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.here(self)), Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), Filters.here(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose 1 Force");

            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        return actions;
    }
}
