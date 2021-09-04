package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.SoupEatenEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Dannik Jerriko
 */
public class Card2_084 extends AbstractAlien {
    public Card2_084() {
        super(Side.DARK, 2, 4, null, 3, 2, "Dannik Jerriko", Uniqueness.UNIQUE);
        setLore("Anzati assassin. Cheek-folds hide proboscises which allow him to 'eat the soup' (consume the life Force) of his victims. Smokes t'bac. Currently working for Jabba.");
        setGameText("Once per battle, may use 1 Force to 'eat the soup' of (place out of play) one opposing non-droid character just lost or forfeited at same site. *Power = 1 + total ability of all victims whose soup was eaten.");
        addPersona(Persona.DANNIK);
        addIcons(Icon.A_NEW_HOPE);
        setSpecies(Species.ANZATI);
        addKeyword(Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextPowerModifier(self, new AddEvaluator(new SoupEatenEvaluator(), 1)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.non_droid_character), Filters.sameSite(self))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)) {
            final PhysicalCard lostCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("'Eat the soup' of " + GameUtils.getFullName(lostCard));
            action.setActionMsg("'Eat the soup' of " + GameUtils.getCardLink(lostCard));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            final float ability = game.getModifiersQuerying().getAbility(game.getGameState(), lostCard);
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromOffTableEffect(action, lostCard) {
                                        @Override
                                        protected void cardPlacedOutOfPlay(PhysicalCard card) {
                                            lostCard.setSoupEaten(ability);
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
