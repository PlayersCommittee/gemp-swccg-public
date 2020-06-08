package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Ravager Crash Site
 */
public class Card204_028 extends AbstractSite {
    public Card204_028() {
        super(Side.LIGHT, Title.Ravager_Crash_Site, Title.Jakku);
        setLocationDarkSideGameText("Unless opponent's scavenger present, opponent's Ravager Crash Site game text is canceled.");
        setLocationLightSideGameText("At the start of your turn, may stack top card of Lost Pile face down on Graveyard Of Giants.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Ravager_Crash_Site,
                new UnlessCondition(new PresentCondition(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.scavenger))),
                game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerOnLightSideOfLocation)) {
            PhysicalCard topCardOfLostPile = game.getGameState().getTopOfLostPile(playerOnLightSideOfLocation);
            if (topCardOfLostPile != null) {
                PhysicalCard graveyardOfGiants = Filters.findFirstActive(game, self, Filters.Graveyard_Of_Giants);
                if (graveyardOfGiants != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
                    action.setText("Stack card on Graveyard Of Giants");
                    action.setActionMsg("Stack top card of Lost Pile on " + GameUtils.getCardLink(graveyardOfGiants));
                    // Perform result(s)
                    action.appendEffect(
                            new StackOneCardFromLostPileEffect(action, topCardOfLostPile, graveyardOfGiants, true, false, true));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}