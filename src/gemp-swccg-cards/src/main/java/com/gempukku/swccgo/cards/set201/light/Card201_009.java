package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPersonalForceGenerationModifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Seeking An Audience (V)
 */
public class Card201_009 extends AbstractNormalEffect {
    public Card201_009() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Seeking_An_Audience, Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'With your wisdom, I'm sure that we can work out an arrangement which will be mutually beneficial and enable us to avoid any unpleasant confrontation.'");
        setGameText("If Han is frozen, deploy on table. Your personal Force generation = 2. Once per turn, may [download] C-3PO, R2-D2, [Jabba's Palace] Lando, or [Jabba's Palace] Leia to a Jabba's Palace site. Once per game, may retrieve R2-D2. [Immune to Alter.]");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE,  Filters.and(Filters.Han, Filters.frozenCaptive));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetPersonalForceGenerationModifier(self, 2, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SEEKING_AN_AUDIENCE__DOWNLOAD_NON_REFLECTIONS_III_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, new HashSet<Persona>(Arrays.asList(Persona.C3PO, Persona.CHEWIE, Persona.LANDO, Persona.LEIA, Persona.R2D2)), Title.Underworld_Contacts)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy C-3PO, R2-D2, [Jabba's Palace] Lando, or [Jabba's Palace] Leia to a Jabba's Palace site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action,
                            Filters.or(Filters.C3PO, Filters.R2D2, Filters.and(Icon.JABBAS_PALACE, Filters.Lando), Filters.and(Icon.JABBAS_PALACE, Filters.Leia)),
                            Filters.Jabbas_Palace_site, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.SEEKING_AN_AUDIENCE__RETRIEVE_R2D2;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)){

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve R2-D2");
            action.setActionMsg("Retrieve R2-D2");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.R2D2));
            actions.add(action);
        }
        return actions;
    }
}