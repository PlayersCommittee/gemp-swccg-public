package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
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
public class Card501_030 extends AbstractImperial {
    public Card501_030() {
        super(Side.DARK, 3, 4, 3, 4, 6, "Moff Gideon", Uniqueness.UNIQUE);
        setLore("ISB. Leader");
        setGameText("[Pilot] 2. Rebels here (and at adjacent sites if E-web Blaster here) are power -1. When deployed, may deploy an E-web blaster or any Imperial stormtrooper from Reserve Deck for -3 Force; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_12);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.sameLocation(self)), -1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Rebel, Filters.adjacentSite(self)), new HereCondition(self, Filters.title(Title.E_web_Blaster)), -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MOFF_GIDEON__DOWNLOAD_E_WEB_OR_STORMTROOPER;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an E-web blaster or Imperial stormtrooper");
            action.setActionMsg("Deploy an E-web blaster or Imperial stormtrooper");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.and(Filters.Imperial, Filters.stormtrooper), Filters.title(Title.E_web_Blaster)), -3, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
