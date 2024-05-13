package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyForfeitEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotRemoveJustLostCardsFromLostPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Alien
 * Title: Dannik Jerriko (V)
 */
public class Card215_024 extends AbstractAlien {
    public Card215_024() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Dannik Jerriko", Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Anzati assassin. Cheek-folds hide proboscises which allow him to 'eat the soup' (consume the life Force) of his victims. Smokes t'bac. Currently working for Jabba.");
        setGameText("Opponent's characters just lost from here may not be removed from Lost Pile (except to be placed out of play). While present at a site and armed with a blaster, adds one battle destiny. Cards hit by Dannik are power and forfeit -2.");
        addPersona(Persona.DANNIK);
        addIcons(Icon.A_NEW_HOPE, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        setSpecies(Species.ANZATI);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PresentAtCondition(self, Filters.site), new ArmedWithCondition(self, Filters.blaster)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.isPresentAt(game, self, Filters.site)) {
            // Prevent character removal from Lost Pile
            if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.here(self))) {
                final PhysicalCard lostCard = ((LostFromTableResult) effectResult).getCard();
                if (lostCard != null) {
                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.appendEffect(
                            new AddUntilEndOfTurnModifierEffect(action, new MayNotRemoveJustLostCardsFromLostPileModifier(self, Filters.sameCardId(lostCard)),
                                    GameUtils.getCardLink(lostCard) + " may not be removed from Lost Pile")
                    );
                    actions.add(action);
                }
            }

            // Cards 'hit' by Dannik are power/forfeit -2
            if (TriggerConditions.justHitBy(game, effectResult, Filters.any, self)) {
                PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + GameUtils.getFullName(cardHit) + " power and forfeit - 2.");
                action.setActionMsg("Make " + GameUtils.getCardLink(cardHit) + " power and forfeit - 2.");
                // Perform result(s)
                action.appendEffect(
                        new ModifyPowerEffect(action, cardHit, -2));
                action.appendEffect(
                        new ModifyForfeitEffect(action, cardHit, -2));
                actions.add(action);
            }
        }
        return actions;
    }
}
