package com.gempukku.swccgo.cards.set220.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Character
 * Subtype: Alien
 * Title: Zuckuss (V)
 */
public class Card220_005 extends AbstractAlien {
    public Card220_005() {
        super(Side.DARK, 1, 3, 3, 4, 4, "Zuckuss", Uniqueness.UNIQUE, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Gand. Practitioner of ancient religious findsman vocation. Bounty hunter and scout. Gains surprisingly accurate information through mystical visions during meditation.");
        setGameText("[Pilot] 2. If present (or piloting an [Independent] starship) during battle, may make a just drawn destiny = the printed destiny of the top card of opponent's Lost Pile. Immune to attrition < 2 + the number of weapon destiny draws that were completed this battle.");
        addPersona(Persona.ZUCKUSS);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_20);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
        setSpecies(Species.GAND);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new AddEvaluator(2, new InPlayDataAsFloatEvaluator(self))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if(TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && (GameConditions.isPresent(game, self)
                || GameConditions.isPiloting(game, self, Filters.and(Icon.INDEPENDENT, Filters.starship)))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, opponent)){

            final DrawDestinyState destinyState = game.getGameState().getTopDrawDestinyState();
            final PhysicalCard opponentTopLostCard = game.getGameState().getTopOfLostPile(opponent);

            if(destinyState != null
                && opponentTopLostCard != null){

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Reset destiny draw");
                action.setActionMsg("Reset destiny draw to the printed destiny of the top card of opponent's Lost Pile");
                action.appendUsage(
                        new OncePerBattleEffect(action)
                );
                action.appendEffect(new RefreshPrintedDestinyValuesEffect(action, opponentTopLostCard) {
                    @Override
                    protected void refreshedPrintedDestinyValues() {
                        float printedDestinyValue = opponentTopLostCard.getDestinyValueToUse();
                        action.appendEffect(new SendMessageEffect(action, GameUtils.getCardLink(opponentTopLostCard) + " printed destiny value: "+ printedDestinyValue));
                        action.appendEffect(new ResetDestinyEffect(action, printedDestinyValue));
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyDrawComplete(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, self)) {

            if (GameConditions.cardHasWhileInPlayDataSet(self)) {
                self.setWhileInPlayData(new WhileInPlayData(self.getWhileInPlayData().getFloatValue() + 1));
            } else {
                self.setWhileInPlayData(new WhileInPlayData(1F));
            }
        }

        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.here(self))) {
            self.setWhileInPlayData(null);
        }
        return actions;
    }
}
