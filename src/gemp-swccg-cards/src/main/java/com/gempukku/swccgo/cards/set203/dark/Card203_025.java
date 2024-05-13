package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Imperial
 * Title: Commander Nemet (V)
 */
public class Card203_025 extends AbstractImperial {
    public Card203_025() {
        super(Side.DARK, 2, 2, 1, 2, 4, "Commander Nemet", Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Logistics officer for the Avenger, member of the Line Branch of the Imperial Navy. Relays important scanner information to Captain Needa. Fiercely competitive.");
        setGameText("[Pilot] 2. Once per game, may [download] an Imperial-class Star Destroyer. While piloting Avenger, it is immune to attrition < 4 and during battle may lose an Imperial aboard (or from hand) to cancel a just drawn destiny (causes a redraw if opponent's first battle destiny).");
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.COMMANDER);
        setMatchingStarshipFilter(Filters.Avenger);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.Avenger, Filters.hasPiloting(self)), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COMMANDER_NEMET__DOWNLOAD_IMPERIAL_CLASS_STAR_DESTROYER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy an Imperial-class Star Destroyer from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Imperial_class_Star_Destroyer, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPiloting(game, self, Filters.Avenger)
                && (GameConditions.hasInHand(game, playerId, Filters.Imperial)
                || GameConditions.canSpot(game, self, Filters.and(Filters.Imperial, Filters.aboard(Filters.Avenger))))) {
            final boolean isOpponentsFirstBattleDestiny = (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent) && (((DestinyDrawnResult) effectResult).getNumDestinyDrawnSoFar() == 1));
            if (isOpponentsFirstBattleDestiny ? GameConditions.canCancelDestinyAndCauseRedraw(game, playerId) : GameConditions.canCancelDestiny(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Cancel destiny" + (isOpponentsFirstBattleDestiny ? " and cause re-draw" : ""));
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardFromHandOrOnTableEffect(action, playerId, Filters.and(Filters.Imperial, Filters.or(Filters.inHand(playerId), Filters.aboard(Filters.Avenger)))) {
                            @Override
                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                // Pay cost(s)
                                if (Filters.inHand(playerId).accepts(game, selectedCard)) {
                                    action.appendCost(
                                            new LoseCardFromHandEffect(action, selectedCard));
                                } else {
                                    action.appendCost(
                                            new LoseCardFromTableEffect(action, selectedCard, true));
                                }
                                // Perform result(s)
                                if (isOpponentsFirstBattleDestiny) {
                                    action.appendEffect(
                                            new CancelDestinyAndCauseRedrawEffect(action));
                                } else {
                                    action.appendEffect(
                                            new CancelDestinyEffect(action));
                                }
                            }

                            @Override
                            public String getChoiceText(int numCardsToChoose) {
                                return "Choose Imperial to lose";
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
