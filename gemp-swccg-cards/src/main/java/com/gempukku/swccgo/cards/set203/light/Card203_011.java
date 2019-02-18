package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 & C-3PO
 */
public class Card203_011 extends AbstractDroid {
    public Card203_011() {
        super(Side.LIGHT, 3, 4, 2, 7, "R2-D2 & C-3PO", Uniqueness.UNIQUE);
        addComboCardTitles("R2-D2", "C-3PO");
        setLore("Stranded in the Dune Sea, R2-D2 insisted on heading into rocky canyons where he thought settlements were likely to exist. Threepio had other ideas.");
        setGameText("If about to leave table (even from Overwhelmed) from Tatooine system and Stolen Data Tapes deployed on this character, may instead be relocated to Dune Sea. Immune to A Gift, Firepower, devices, and opponent's Interrupts.");
        addPersonas(Persona.R2D2, Persona.C3PO);
        addIcons(Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_3);
        addModelTypes(ModelType.ASTROMECH, ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.A_Gift));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Firepower));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.device));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.opponents(self), Filters.Interrupt)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && GameConditions.isAtLocation(game, self, Filters.Tatooine_system)
                && GameConditions.hasAttached(game, self, Filters.Stolen_Data_Tapes)) {
            PhysicalCard duneSea = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.Dune_Sea, Filters.locationCanBeRelocatedTo(self, false, false, true, 0, false)));
            if (duneSea != null) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Relocate to Dune Sea");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(duneSea));
                action.addAnimationGroup(duneSea);
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(self);
                                for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                    result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                                }
                            }
                        });
                action.appendEffect(
                        new RelocateBetweenLocationsEffect(action, self, duneSea));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
