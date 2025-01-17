package com.gempukku.swccgo.cards.set220.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 20
 * Type: Starship
 * Subtype: Starfighter
 * Title: Millennium Falcon (V)
 */
public class Card220_007 extends AbstractStarfighter {
    public Card220_007() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 6, 7, "Millennium Falcon", Uniqueness.UNIQUE, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setLore("Modified YT-1300 freighter. Owned by Lando Calrissian until won by Han in a sabacc game. 26.7 meters long. 'She may not look like much, but she's got it where it counts.'");
        setGameText("May add 2 pilots and 2 passengers. Once per game, may [download] Han or Chewie aboard. While [A New Hope] Han piloting, Force drain +1 here. While Han or Chewie piloting, immune to attrition < 5 (< 7 if both).");
        addPersona(Persona.FALCON);
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_20);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Han, Filters.Chewie));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MILLENNIUM_FALCON__DOWNLOAD_HAN_OR_CHEWIE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.CHEWIE)
                || (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.HAN)))) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Han or Chewie aboard");
            action.setActionMsg("Deploy Han or Chewie aboard from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.or(Filters.Han, Filters.Chewie), Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition hanPiloting = new HasPilotingCondition(self, Filters.Han);
        Condition chewiePiloting = new HasPilotingCondition(self, Filters.Chewie);
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OrCondition(hanPiloting, chewiePiloting), new ConditionEvaluator(5, 7, new AndCondition(hanPiloting, chewiePiloting))));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new HasPilotingCondition(self, Filters.and(Icon.A_NEW_HOPE, Filters.Han)), 1, playerId));
        return modifiers;
    }
}
