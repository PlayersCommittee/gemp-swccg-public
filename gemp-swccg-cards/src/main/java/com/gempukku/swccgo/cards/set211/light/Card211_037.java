package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: System
 * Title: Takodana
 */
public class Card211_037 extends AbstractSystem {
    public Card211_037() {
        super(Side.LIGHT, Title.Takodana, 5);
        setLocationLightSideGameText("During your deploy phase, if you just deployed a Maz's Castle location, may draw top card of Reserve Deck.");
        setLocationDarkSideGameText("If you just deployed a starship here, may shuffle any Deck or Pile.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_11, Icon.EPISODE_VII);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.Takodana__Draw_Top_Card_Of_Reserve_Deck;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, playerOnLightSideOfLocation, Filters.Mazs_Castle_Location)
                && GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            action.setActionMsg("Draw top card of Reserve Deck");
            action.appendUsage(new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, self.getOwner()));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        if (TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.owner(playerOnDarkSideOfLocation), Filters.starship), Filters.Takonada_system))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Shuffle any deck or pile");
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerOnDarkSideOfLocation, Filters.or(Zone.RESERVE_DECK, Zone.LOST_PILE, Zone.USED_PILE, Zone.FORCE_PILE)) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.allowResponses("Shuffle " + cardPileOwner + "'s " + cardPile.getHumanReadable(),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new ShufflePileEffect(action, cardPileOwner, cardPile));
                                        }
                                    });

                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}