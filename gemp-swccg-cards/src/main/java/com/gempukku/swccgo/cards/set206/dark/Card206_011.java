package com.gempukku.swccgo.cards.set206.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 6
 * Type: Character
 * Subtype: Imperial
 * Title: Veers (V)
 */
public class Card206_011 extends AbstractImperial {
    public Card206_011() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Veers", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("General of the AT-AT assault armor division sent by Darth Vader to crush the Rebellion on Hoth. Cold and ruthless.");
        setGameText("Adds 3 to the power of anything he pilots. Leader. If piloting Blizzard 1 in battle, adds one destiny to total power. Deploys free aboard Blizzard 1. Once per turn, may reveal from hand to deploy 6th Marker or Blizzard 1 to Hoth from Reserve Deck; reshuffle.");
        addPersona(Persona.VEERS);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LeaderModifier(self));
        modifiers.add(new DeploysFreeAboardModifier(self, Persona.BLIZZARD_1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsDestinyToPowerModifier(self, new AndCondition(new InBattleCondition(self), new PilotingCondition(self, Filters.Blizzard_1)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VEERS__UPLOAD_HOTH_COMBAT_VEHICLE_OR_SIXTH_MARKER;

        if (GameConditions.isOncePerTurn(game, self, gameTextSourceCardId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy 6th Marker or Blizzard 1");
            action.setActionMsg("Deploy 6th Marker or Blizzard 1");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.Blizzard_1, Filters.Sixth_Marker), Title.Hoth, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}