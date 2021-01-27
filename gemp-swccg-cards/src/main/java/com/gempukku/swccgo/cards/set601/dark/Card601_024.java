package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Starship
 * Subtype: Starfighter
 * Title: Slave I, Symbol Of Fear
 */
public class Card601_024 extends AbstractStarfighter {
    public Card601_024() {
        super(Side.DARK, 2, 4, 3, null, 4, 4, 6, "Slave I, Symbol Of Fear", Uniqueness.UNIQUE);
        setLore("Originally designed as a planetary defense craft. Uses restricted jamming technology, allowing it to appear out of nowhere. Contains many hidden armaments.");
        setGameText("May reveal from hand to take a [Block 8] Fett from Reserve Deck; reshuffle; and deploy both simultaneously. May add 2 pilots and 2 passengers. Your [Block 8] Fetts deploy -2 aboard. Immune to attrition < 5.");
        addPersona(Persona.SLAVE_I);
        addIcons(Icon.CLOUD_CITY, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.BLOCK_8);
        addModelType(ModelType.FIRESPRAY_CLASS_ATTACK_SHIP);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Jango_Fett, Filters.Boba_Fett));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.and(Filters.Fett, Filters.icon(Icon.BLOCK_8)), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.and(Filters.Fett, Filters.icon(Icon.BLOCK_8)), -2, self));
        return modifiers;
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SLAVE_I__UPLOAD_AND_DEPLOY_FETT;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy with a Fett");
            action.setActionMsg("Reveal to deploy with a Fett");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, Filters.and(Filters.Fett, Filters.icon(Icon.BLOCK_8)), true));
            return Collections.singletonList(action);
        }

        return null;
    }
}