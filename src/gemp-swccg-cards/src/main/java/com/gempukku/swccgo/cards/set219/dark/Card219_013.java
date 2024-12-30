package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Imperial Complex
 */
public class Card219_013 extends AbstractSite {
    public Card219_013() {
        super(Side.DARK, Title.Lothal_Imperial_Complex, Title.Lothal, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("If you control with a leader, once per battle at a related site, you may deploy a card as a 'react'.");
        setLocationLightSideGameText("Unless you occupy, your spies may not deploy here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition unlessYouOccupy = new UnlessCondition(new OccupiesCondition(playerOnLightSideOfLocation, self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.spy), unlessYouOccupy, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        Condition oncePerBattleCondition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerOnDarkSideOfLocation, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_1).getUsedLimit()<1;
            }
        };

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a card as a react",
                new AndCondition(oncePerBattleCondition, new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.leader)),
                playerOnDarkSideOfLocation,
                Filters.or(Filters.character, Filters.vehicle, Filters.starship, Filters.weapon, Filters.device),
                Filters.and(Filters.battleLocation, Filters.relatedSite(self))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isReact(game, effect)
                && effect.getAction()!=null) {
            PhysicalCard source = effect.getAction().getActionSource();

            // if this card is the source of the react then increment the per battle limit so the condition above can check for it
            if (source != null
                    && Filters.sameCardId(self).accepts(game, source)) {
                game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerOnDarkSideOfLocation, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_1).incrementToLimit(1,1);
            }
        }

        return null;
    }
}
