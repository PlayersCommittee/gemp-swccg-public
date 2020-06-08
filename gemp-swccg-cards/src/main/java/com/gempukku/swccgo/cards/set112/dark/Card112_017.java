package com.gempukku.swccgo.cards.set112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Effect
 * Title: Power Of The Hutt
 */
public class Card112_017 extends AbstractNormalEffect {
    public Card112_017() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Power Of The Hutt", Uniqueness.UNIQUE);
        setLore("Jabba runs his organization out of a palace built around a B'omarr monastery. His fortress near the border of the western Dune Sea is safe from enemies in Mos Eisley.");
        setGameText("Deploy on table. Once during each of your turns, may deploy one Boelo, Bib, Ephant Mon, Jabba's Sail Barge, Jabba's Space Cruiser, or Hutt Influence from Reserve Deck; reshuffle. Also, your aliens aboard Jabba's Sail Barge are immune to attrition < 6. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.POWER_OF_THE_HUTT__DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.JABBAS_SAIL_BARGE,
                Arrays.asList(Title.Boelo, Title.Bib, Title.Ephant_Mon, Title.Jabbas_Space_Cruiser, Title.Hutt_Influence))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Boelo, Bib, Ephant Mon, Jabba's Sail Barge, Jabba's Space Cruiser, or Hutt Influence from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Boelo, Filters.Bib, Filters.Ephant_Mon,
                            Filters.Jabbas_Sail_Barge, Filters.Jabbas_Space_Cruiser, Filters.Hutt_Influence), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.alien,
                Filters.aboardStarshipOrVehicleOfPersona(Persona.JABBAS_SAIL_BARGE)), 6));
        return modifiers;
    }
}