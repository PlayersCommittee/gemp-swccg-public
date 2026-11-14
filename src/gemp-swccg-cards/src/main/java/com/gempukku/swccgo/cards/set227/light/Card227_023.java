package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 27
 * Type: Effect
 * Title: Sacrifice For Something Bigger (front)
 */
public class Card227_023 extends AbstractNormalEffect {
    public Card227_023() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Sacrifice_For_Something_Bigger, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("If Zero Hour on table, deploy on table. Once per game, may [DOWNLOAD] Kanan. If Kanan just lost, place him out of play, relocate this Effect to a Lothal site, and flip this card. [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_27);
        addImmuneToCardTitle(Title.Alter);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // Double-sided card: may only deploy at the start of the game (e.g. via a starting interrupt like
        // Heading For The Medical Frigate, which deploys free "deploy on table" Effects from Reserve Deck),
        // never floated in mid-game. Requires your Zero Hour on table.
        return GameConditions.isDuringStartOfGame(game)
            && Filters.canSpot(game, self, Filters.and(Filters.your(self), Filters.title("Zero Hour")));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SACRIFICE_FOR_SOMETHING_BIGGER__DEPLOY_KANAN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.KANAN)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Kanan from Reserve Deck");
            action.setActionMsg("Deploy Kanan from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Kanan, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s): "If Kanan just lost..."
        if (TriggerConditions.justLost(game, effectResult, Filters.Kanan)) {
            final PhysicalCard kanan = ((LostFromTableResult) effectResult).getCard();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Place Kanan out of play, relocate, and flip");
            action.setActionMsg("Place " + GameUtils.getCardLink(kanan) + " out of play, relocate " + GameUtils.getCardLink(self) + " to a Lothal site, and flip it");
            // Perform result(s)
            action.appendEffect(
                new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, playerId, kanan, false));
            // Relocate this Effect to a Lothal site (if possible), then flip it.
            // Move it *to* the location (moveCardToLocation) rather than attaching it, so the
            // flipped Effect is "at" the site and can move like a character.
            action.appendEffect(
                new ChooseCardOnTableEffect(action, playerId, "Choose Lothal site to relocate to", Filters.Lothal_site) {
                    @Override
                    protected void cardSelected(final PhysicalCard lothalSite) {
                        action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    game.getGameState().sendMessage(GameUtils.getCardLink(self) + " relocates to " + GameUtils.getCardLink(lothalSite));
                                    game.getGameState().moveCardToLocation(self, lothalSite);
                                }
                            });
                        action.appendEffect(
                            new FlipCardEffect(action, self));
                    }
                });
            return Collections.singletonList(action);
        }
        return null;
    }
}
