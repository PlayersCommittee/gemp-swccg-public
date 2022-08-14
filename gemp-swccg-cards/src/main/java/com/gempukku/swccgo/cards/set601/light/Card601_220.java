package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: BLock 2
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Luke Skywalker (V)
 */
public class Card601_220 extends AbstractRebel {
    public Card601_220() {
        super(Side.LIGHT, 1, 4, 4, 4, 7, "Commander Luke Skywalker", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Hero of Yavin. Promoted to Commander in his third year of military training with the Alliance. Squadron flight leader at Echo Base during the Battle of Hoth.");
        setGameText("[Pilot] 3. While piloting Rogue 1, it is maneuver +2 and Luke draws two battle destiny if unable to otherwise. During your control phase, may use 1 Force to [upload] a speeder or a card with 'T-47' in game text. Immune to attrition < 3.");
        addPersona(Persona.LUKE);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_2);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.ROGUE_SQUADRON);
        setMatchingVehicleFilter(Filters.Rogue_1);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePilotingRogue1 = new PilotingCondition(self, Filters.Rogue_1);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.Rogue_1, whilePilotingRogue1, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingRogue1, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_LUKE_SKYWALKER__UPLOAD_SPEEDER_OR_T47_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a speeder or a card with 'T-47' in game text into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.speeder, Filters.gameTextContains("T\\-47"), Filters.gameTextContains("T\\-47s")), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
