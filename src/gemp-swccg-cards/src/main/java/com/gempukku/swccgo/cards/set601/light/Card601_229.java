package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker (V)
 */
public class Card601_229 extends AbstractRebel {
    public Card601_229() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Luke Skywalker", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Son of Anakin Skywalker. Student of Obi-Wan Kenobi. Honed piloting skills while bullseyeing womp rats in Beggar's Canyon aboard T-16 skyhopper.");
        setGameText("[Pilot] 3. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may [upload] one Darklighter Spin or retrieve 1 Force.");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AttackRunTotalModifier(self, new PilotingCondition(self, Filters.lead_starfighter_in_Attack_Run), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId1 = GameTextActionId.OTHER_CARD_ACTION_1;
        GameTextActionId gameTextActionId2 = GameTextActionId.LUKE_SKYWALKER_V__UPLOAD_DARKLIGHTER_SPIN;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId1, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId2, Phase.CONTROL)
                && GameConditions.isPilotingAt(game, self, Filters.battleground)) {

            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action1.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerPhaseEffect(action1));
            // Perform result(s)
            action1.appendEffect(
                    new RetrieveForceEffect(action1, playerId, 1));
            actions.add(action1);

            // Check condition(s)
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {

                final TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
                action2.setText("Take Darklighter Spin into hand from Reserve Deck");
                // Update usage limit(s)
                action2.appendUsage(
                        new OncePerPhaseEffect(action2));
                // Perform result(s)
                action2.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action2, playerId, Filters.Darklighter_Spin, true));
                actions.add(action2);
            }
        }
        return actions;
    }
}
