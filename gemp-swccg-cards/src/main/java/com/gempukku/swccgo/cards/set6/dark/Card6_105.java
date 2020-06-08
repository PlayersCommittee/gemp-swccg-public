package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.CancelAttackEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Gamorrean Guard
 */
public class Card6_105 extends AbstractAlien {
    public Card6_105() {
        super(Side.DARK, 2, 4, 4, 1, 1, Title.Gamorrean_Guard);
        setLore("Big. Strong. Dumb.");
        setGameText("Deploys only to Tatooine. Power -1 when not at a Tatooine site. May be sacrificed (lost) to cancel an attack just initiated by a creature present.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.GAMORREAN);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_at_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(new AtCondition(self, Filters.Tatooine_site)), -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.attackInitiatedAt(game, effectResult, Filters.sameLocation(self))) {
            Filter creaturePresentFilter = Filters.and(Filters.creature, Filters.present(self));
            AttackState attackState = game.getGameState().getAttackState();
            if (attackState != null
                    && ((attackState.isCreaturesAttackingEachOther() && Filters.canSpot(attackState.getAllCardsParticipating(), game, creaturePresentFilter))
                    || (attackState.isCreatureAttackingNonCreature() && Filters.canSpot(attackState.getCardsAttacking(), game, creaturePresentFilter)))) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Sacrifice");
                action.setActionMsg("Sacrifice " + GameUtils.getCardLink(self) + " to cancel attack");
                // Pay cost(s)
                action.appendCost(
                        new LoseCardFromTableEffect(action, self));
                // Perform result(s)
                action.appendEffect(
                        new CancelAttackEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
