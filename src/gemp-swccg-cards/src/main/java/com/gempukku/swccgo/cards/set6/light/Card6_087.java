package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: System
 * Title: Tibrin
 */
public class Card6_087 extends AbstractSystem {
    public Card6_087() {
        super(Side.LIGHT, Title.Tibrin, 2, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLocationDarkSideGameText("If you control, Force drain +1 here.");
        setLocationLightSideGameText("If you occupy, during your control phase, may exchange three cards in hand for any one card in your Lost Pile.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.numCardsInHand(game, playerOnLightSideOfLocation) >= 3
                && GameConditions.hasLostPile(game, playerOnLightSideOfLocation)
                && GameConditions.occupies(game, playerOnLightSideOfLocation, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Exchange cards with in Lost Pile");
            action.setActionMsg("Exchange 3 cards in hand with a card in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardsInHandWithCardInCardPileEffect(action, playerOnLightSideOfLocation, Zone.LOST_PILE, 3, 3, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}