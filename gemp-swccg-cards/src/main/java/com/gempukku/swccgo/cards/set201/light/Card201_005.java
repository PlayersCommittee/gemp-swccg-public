package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Character
 * Subtype: Rebel
 * Title: Princess Leia (V)
 */
public class Card201_005 extends AbstractRebel {
    public Card201_005() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Princess Leia", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Prominent leader in the struggling Alliance. Former member of the Imperial Senate. Beginning to discover her true heritage. Likes scoundrels.");
        setGameText("Leia's deploy cost may not be modified by opponent. Adds one battle destiny if with Han or Vader. May move as a 'react' to same site as an Imperial. Once per turn, may [download] Leia Of Alderaan or Reflection. Immune to attrition < 4.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_1);
        addKeywords(Keyword.LEADER, Keyword.FEMALE, Keyword.SENATOR);
        addPersona(Persona.LEIA);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveDeployCostModifiedModifier(self, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.or(Filters.Han, Filters.Vader)), 1));
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.sameSiteAs(self, Filters.Imperial)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PRINCESS_LEIA__DOWNLOAD_LEIA_OF_ALDERAAN_OR_REFLECTION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Leia_Of_Alderaan, Title.Reflection))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Leia_Of_Alderaan, Filters.Reflection), true));
            return Collections.singletonList(action);
        }

        return null;
    }
}
