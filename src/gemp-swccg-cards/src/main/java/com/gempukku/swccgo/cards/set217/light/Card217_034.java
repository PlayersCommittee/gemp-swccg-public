package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CommuningCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ChoiceMadeResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Location
 * Subtype: Site
 * Title: Endor: Anakin's Funeral Pyre
 */
public class Card217_034 extends AbstractSite {
    public Card217_034() {
        super(Side.LIGHT, Title.Anakins_Funeral_Pyre, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLocationDarkSideGameText("");
        setLocationLightSideGameText("If you just chose I Have It on your [Skywalker] Epic Event, [download] Like My Father Before Me. If Anakin 'communing,' you may initiate battles for free.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SKYWALKER, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANAKINS_FUNERAL_PYRE__DEPLOY_EFFECT;

        if (TriggerConditions.justMadeChoice(game, effectResult, playerOnLightSideOfLocation, Filters.and(Icon.SKYWALKER, Filters.Epic_Event))
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, true, false)
                && "I Have It".equals(((ChoiceMadeResult) effectResult).getChoice())) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerOnLightSideOfLocation);
            action.setText("Deploy Like My Father Before Me");
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Like_My_Father_Before_Me), GameConditions.isDuringStartOfGame(game), !GameConditions.isDuringStartOfGame(game)));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, Filters.any, new CommuningCondition(Filters.Anakin), playerOnLightSideOfLocation));
        return modifiers;
    }
}