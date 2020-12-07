package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Sabe
 */
public class Card12_025 extends AbstractRepublic {
    public Card12_025() {
        super(Side.LIGHT, 3, 2, 1, 2, 2, "Sabe", Uniqueness.UNIQUE);
        setLore("Female chosen as a handmaiden by Panaka due to her resemblance to Amidala. Trained to play the role of the Queen whenever it is considered that Amidala may be at risk.");
        setGameText("While at same site as Amidala, Sabe is power +2 and Amidala is immune to attrition. When at a site, during your deploy phase may place Sabe out of play to deploy Amidala to that site (for free) from your Reserve Deck (reshuffle) or Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE, Keyword.HANDMAIDEN);
        addPersona(Persona.SABE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atSameSiteAsAmidala = new AtSameSiteAsCondition(self, Filters.Amidala);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.Sabe, atSameSiteAsAmidala, 2));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Amidala, atSameSiteAsAmidala));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.SABE__DOWNLOAD_AMIDALA;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
            if (Filters.site.accepts(game, location)) {
                if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.AMIDALA)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Deploy Amidala from Reserve Deck");
                    // Pay cost(s)
                    action.appendCost(
                            new PlaceCardOutOfPlayFromTableEffect(action, self));
                    // Perform result(s)
                    action.appendEffect(
                            new DeployCardToLocationFromReserveDeckEffect(action, Filters.Amidala, Filters.sameCardId(location), true, true));
                    actions.add(action);
                }

                if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.AMIDALA)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Deploy Amidala from Lost Pile");
                    // Pay cost(s)
                    action.appendCost(
                            new PlaceCardOutOfPlayFromTableEffect(action, self));
                    // Perform result(s)
                    action.appendEffect(
                            new DeployCardToLocationFromLostPileEffect(action, Filters.Amidala, Filters.sameCardId(location), true, false));
                    actions.add(action);
                }
            }
        }
        return actions;
    }
}
