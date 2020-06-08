package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Skrilling
 */
public class Card6_123 extends AbstractAlien {
    public Card6_123() {
        super(Side.DARK, 4, 2, 2, 1, 2, "Skrilling");
        setLore("Regarded as whiners. Skrillings are a scavenger species. Steal from corpses left behind on battlefields. Feed on carrion and uncooked meat. Avoided by many species.");
        setGameText("Once per turn, may steal a weapon or device from an opponent's character just lost or forfeited where present. When you play Tusken Scavengers, may steal vehicles, weapons and devices found (place them in your Used Pile).");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.SCAVENGER);
        setSpecies(Species.SKRILLING);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.wherePresent(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            Collection<PhysicalCard> devicesAndWeapons = Filters.filter(justLostCard.getCardsPreviouslyAttached(), game, self,
                    TargetingReason.TO_BE_STOLEN, Filters.and(Filters.or(Filters.weapon, Filters.device), Filters.inLostPile(game.getOpponent(playerId))));
            if (!devicesAndWeapons.isEmpty()) {
                List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
                for (PhysicalCard cardToSteal : devicesAndWeapons) {

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

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Tusken_Scavengers),
                ModifyGameTextType.TUSKEN_SCAVENGERS__MAY_STEAL_CARDS_FOUND));
        return modifiers;
    }
}
