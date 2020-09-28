package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blue 11
 */

public class Card209_032 extends AbstractCombatVehicle {
    public Card209_032() {
        super(Side.LIGHT, 4, 4, 4, null, 4, 4, 6, "Blue 11", Uniqueness.UNIQUE);
        setLore("Enclosed");
        setGameText("May add 1 pilot and 2 passengers. Permanent pilot provides ability of 2. Once per turn, may [download] a non-pilot Rebel of ability < 5 aboard. Immune to attrition < 4.");
        addModelType(ModelType.U_WING);
        addIcons(Icon.PILOT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.ENCLOSED, Keyword.UWING, Keyword.BLUE_SQUADRON);
        setPilotCapacity(1);
        setPassengerCapacity(2);
        addPersona(Persona.BLUE_11);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {
        });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLUE_11__DOWNLOAD_NON_PILOT_REBEL_ABILITY_LESS_THAN_5;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy non-pilot Rebel of ability < 5");
            action.setActionMsg("Deploy non-pilot Rebel of ability < 5 from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.and(Filters.non_pilot_character, Filters.Rebel, Filters.abilityLessThan(5)), Filters.sameCardId(self), false , true));

            return Collections.singletonList(action);

        }
        return null;
    }
}

