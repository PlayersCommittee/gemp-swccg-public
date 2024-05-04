package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Character
 * Subtype: Imperial
 * Title: Juno Eclipse, Black Leader
 */
public class Card601_089 extends AbstractImperial {
    public Card601_089() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Juno Eclipse, Black Leader", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("");
        setGameText("Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for her.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3.");
        addPersona(Persona.JUNO);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.BLACK_SQUADRON);
        setMatchingWeaponFilter(Filters.title("Naboo Blaster"));
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -1, Filters.starship));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.Galen, Filters.atSameSite(self))), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__JUNO_ECLIPSE__RETRIEVE_CARD_WITH_BACK_INTO_HAND;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a card with 'back' in title");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, false, Filters.or(Filters.titleContains("back"), Filters.titleContains("backs"))));

            actions.add(action);
        }

        return actions;
    }
}
