package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OutOfPlayCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: Resistance
 * Title: Finn, Resistance Hero
 */
public class Card225_045 extends AbstractResistance {
    public Card225_045() {
        super(Side.LIGHT, 1, 4, 4, 4, 6, "Finn, Resistance Hero", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Leader.");
        setGameText("During battle, if Luke or Paige out of play (or Rose or Jannah here), adds one destiny to total power. Once per game, may [download] a non-leader Resistance character of lesser ability here. Jedi Lightsaber may deploy on Finn. Immune to attrition < 4.");
        addPersona(Persona.FINN);
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition lukeOrPaigeOutOfPlay = new OutOfPlayCondition(self, Filters.or(Filters.Luke, Filters.Paige));
        Condition roseOrJannahHere = new HereCondition(self, Filters.or(Filters.Rose, Filters.Jannah));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new OrCondition(lukeOrPaigeOutOfPlay, roseOrJannahHere), 1));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.Jedi_Lightsaber, self));
        modifiers.add(new MayUseWeaponModifier(self, Filters.Jedi_Lightsaber));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.FINN_RESISTANCE_HERO__DOWNLOAD_CHARACTER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            
            Filter abilityLessThanSelf = new Filter() {
                @Override
                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                    return modifiersQuerying.getAbility(gameState, physicalCard) < modifiersQuerying.getAbility(gameState, self);
                }
            };

            Filter downloadFilter = Filters.and(Filters.resistance, Filters.character, Filters.not(Filters.leader), abilityLessThanSelf);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Resistance character from Reserve Deck");
            action.setActionMsg("Deploy non-leader Resistance character of lesser ability from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, downloadFilter, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
