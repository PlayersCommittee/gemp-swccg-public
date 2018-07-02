package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithPresentCondition;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Echo Med Lab
 */
public class Card3_060 extends AbstractSite {
    public Card3_060() {
        super(Side.LIGHT, Title.Echo_Med_Lab, Title.Hoth);
        setLocationDarkSideGameText("If you control, with an Imperial present, Force drain +1 here.");
        setLocationLightSideGameText("Once per turn, one of your medical droids is deploy -2.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsWithPresentCondition(playerOnDarkSideOfLocation, self, Filters.Imperial), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(final String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.medical_droid),
                    new Condition() {
                        @Override
                        public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                            PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                            return modifiersQuerying.getUntilEndOfTurnLimitCounter(self, playerOnLightSideOfLocation, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_DEFAULT).getUsedLimit() < 1;
                        }
                    }, -2, Filters.any));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.medical_droid))) {
            game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerOnLightSideOfLocation, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
        }
        return null;
    }
}