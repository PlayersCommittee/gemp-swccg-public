package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Alien
 * Title: Lord Idris Adenn, Voice
 */
public class Card302_029 extends AbstractAlien {
    public Card302_029() {
        super(Side.DARK, 1, 5, 5, 4, 7, "Lord Idris Adenn, Voice", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setArmor(5);
		setLore("Also known as Haar Dreki’ormr be’Manda'yaim (The Dragonsnake of Mandalore) or, in Basic The Viper, is a cocky, headstrong, and dangerous Mandalorian Bounty Hunter and Leader.");
        setGameText("May use two different weapons. May use any 'stolen' lightsaber. Once per turn, may steal a lightsaber from an opponent's character just lost where present. Immune to attrition < 3 (< 5 while armed with a lightsaber). May 'fly' (landspeed = 3). ");
        addIcon(Icon.WARRIOR, 2);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.LEADER, Keyword.DARK_COUNCILOR);
        addPersona(Persona.IDRIS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.lightsaber)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5, new ArmedWithCondition(self, Filters.lightsaber))));
		modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.wherePresent(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            Collection<PhysicalCard> lightsabers = Filters.filter(justLostCard.getCardsPreviouslyAttached(), game, self,
                    TargetingReason.TO_BE_STOLEN, Filters.and(Filters.lightsaber, Filters.inLostPile(game.getOpponent(playerId))));
            if (!lightsabers.isEmpty()) {
                List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
                for (PhysicalCard cardToSteal : lightsabers) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Steal " + GameUtils.getFullName(cardToSteal) + " from " + GameUtils.getFullName(justLostCard));
                    action.setActionMsg("Steal " + GameUtils.getCardLink(cardToSteal) + " from " + GameUtils.getCardLink(justLostCard));
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerTurnEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new StealCardAndAttachFromLostPileEffect(action, playerId, self, Filters.sameCardId(cardToSteal)));
                    actions.add(action);
                }
                return actions;
            }
        }
        return null;
    }
}
