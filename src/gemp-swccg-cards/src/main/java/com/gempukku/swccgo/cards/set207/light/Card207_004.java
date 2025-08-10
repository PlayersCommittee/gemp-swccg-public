package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToDrawDestinyCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Character
 * Subtype: Alien
 * Title: Dice Ibegon (V)
 */
public class Card207_004 extends AbstractAlien {
    public Card207_004() {
        super(Side.LIGHT, 2, 3, 2, 3, 5, Title.Dice_Ibegon, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Female companion of Lak Sivrak. A Florn lamproid. Dangerous predator with a poison stinger. Rumored to have mysterious time-shifting attunement to the Force.");
        setGameText("Power +2 at a Jabba's Palace or Endor site. If just lost, place Dice in Used Pile. If opponent is about to draw destiny for an Interrupt targeting your character here, opponent must first lose 1 Force, then shuffle their Reserve Deck.");
        addIcons(Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.FLORN_LAMPROID);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Jabbas_Palace_site, Filters.Endor_site)), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isAboutToDrawDestiny(game, effectResult, opponent)) {
            PhysicalCard actionSource = ((AboutToDrawDestinyCardResult) effectResult).getActionSource();
            if (actionSource != null
                    && Filters.and(Filters.Interrupt, Filters.cardBeingPlayedTargeting(self, Filters.and(Filters.your(self), Filters.character, Filters.here(self)))).accepts(game, actionSource)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make opponent lose Force and shuffle");
                action.setActionMsg("Make opponent lose 1 Force and shuffle Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                action.appendEffect(
                        new ShuffleReserveDeckEffect(action, opponent, opponent));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
