package com.gempukku.swccgo.cards.set212.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: Imperial
 * Title: Moff Gideon
 */
public class Card212_002 extends AbstractImperial {
    public Card212_002() {
        super(Side.DARK, 3, 4, 3, 4, 6, "Moff Gideon", Uniqueness.UNIQUE);
        setLore("ISB. Leader");
        setGameText("[Pilot] 2. Rebels here (and at adjacent sites if your E-web blaster here) are power -1. When deployed, may \\/ an E-web blaster or an Imperial stormtrooper here for -2 Force.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_12);
        addPersona(Persona.GIDEON);
        addKeywords(Keyword.LEADER, Keyword.MOFF);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.atSameLocation(self)), -1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.at(Filters.adjacentSite(self))), new HereCondition(self, Filters.title(Title.E_web_Blaster)), -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MOFF_GIDEON__DOWNLOAD_E_WEB_OR_STORMTROOPER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an E-web blaster or Imperial stormtrooper here");
            action.setActionMsg("Deploy an E-web blaster or Imperial stormtrooper here");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.Imperial, Filters.stormtrooper), Filters.title(Title.E_web_Blaster)), Filters.here(self), -2, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
