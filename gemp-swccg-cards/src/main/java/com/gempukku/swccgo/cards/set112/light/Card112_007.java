package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostUsingLandspeedModifier;

import java.util.*;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Effect
 * Title: Seeking An Audience
 */
public class Card112_007 extends AbstractNormalEffect {
    public Card112_007() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Seeking_An_Audience, Uniqueness.UNIQUE);
        setLore("'With your wisdom, I'm sure that we can work out an arrangement which will be mutually beneficial and enable us to avoid any unpleasant confrontation.'");
        setGameText("Deploy on table. Once during each of your turns, may deploy one Underworld Contacts, [Jabba's Palace] Lando, [Jabba's Palace] Leia, R2-D2, or C-3PO from Reserve Deck; reshuffle. Also, while Luke is at a Jabba's Palace site, opponent's aliens there must use +1 Force to use their landspeed.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SEEKING_AN_AUDIENCE__DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId,
                new HashSet<Persona>(Arrays.asList(Persona.LANDO, Persona.LEIA, Persona.R2D2, Persona.C3PO)), Title.Underworld_Contacts)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Underworld Contacts, [Jabba's Palace] Lando, [Jabba's Palace] Leia, R2-D2, or C-3PO from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Underworld_Contacts, Filters.and(Icon.JABBAS_PALACE, Filters.Lando),
                            Filters.and(Icon.JABBAS_PALACE, Filters.Leia), Filters.R2D2, Filters.C3PO), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostUsingLandspeedModifier(self, Filters.and(Filters.opponents(self), Filters.alien,
                Filters.at(Filters.sameSiteAs(self, Filters.Luke))), new AtCondition(self, Filters.Luke, Filters.Jabbas_Palace_site), 1));
        return modifiers;
    }
}