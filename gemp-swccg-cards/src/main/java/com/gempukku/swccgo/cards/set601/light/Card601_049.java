package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostForSimultaneouslyDeployingPilotModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Starship
 * Subtype: Starfighter
 * Title: Lady Luck
 */
public class Card601_049 extends AbstractStarfighter {
    public Card601_049() {
        super(Side.LIGHT, 3, 3, 3, null, 5, 4, 5, "Lady Luck", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("");
        setGameText("May add 2 pilots and 2 passengers. Scoundrels deploy -1 aboard. May reveal from hand to take into hand [Cloud City] Lando from Reserve Deck; reshuffle; and deploy both simultaneously. While Lando piloting, immune to attrition < 5 (< 6 if at Bespin). ");
        addIcons(Icon.CORUSCANT, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_8);
        addModelType(ModelType.BAUDO_CLASS_STAR_YACHT); //TODO should just be STAR_YACHT
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Lando);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostForSimultaneouslyDeployingPilotModifier(self, Filters.scoundrel, -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.scoundrel, -1, self));
        return modifiers;
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Lando), new ConditionEvaluator(5, 6, new AtCondition(self, Filters.Bespin_system))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LADY_LUCK__UPLOAD_AND_DEPLOY_LANDO;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy with Lando");
            action.setActionMsg("Reveal to deploy with Lando");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, Filters.and(Icon.CLOUD_CITY, Filters.Lando), true));
            return Collections.singletonList(action);
        }

        return null;
    }
}