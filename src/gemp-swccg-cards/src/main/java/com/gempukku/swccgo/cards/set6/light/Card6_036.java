package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringSabaccCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ModifyTotalWeaponDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.WinsDoubleAtSabaccModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: R'kik D'nec, Hero Of The Dune Sea
 */
public class Card6_036 extends AbstractAlien {
    public Card6_036() {
        super(Side.LIGHT, 1, 3, 3, 1, 3,Title.R_kik_D_nec, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("A tribe of Tusken Raiders, a herd of angry banthas, a raging Krayt Dragon and R'kik. Minutes later, the Jawa emerged from the Dune Sea, a bantha tusk over his shoulder.");
        setGameText("Deploys only on Tatooine. Jawa weapons deploy free on R'kik, when firing one, may add up to 3 to the total weapon destiny just drawn. When he is playing Dune Sea Sabacc and wins, he wins double.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Jawa_weapon, Filters.R_kik_D_nec));
        modifiers.add(new WinsDoubleAtSabaccModifier(self, self, new DuringSabaccCondition(Filters.Dune_Sea_Sabacc)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (TriggerConditions.isAboutToCompleteWeaponDestinyDraw(game, effectResult)
                && GameConditions.isDuringWeaponFiringAtTarget(game, Filters.and(Filters.Jawa_weapon, Filters.weaponBeingFiredBy(self)), Filters.any)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add up to 3 to total weapon destiny");

            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose amount to add to total weapon destiny", 1, 3, 3) {
                                @Override
                                public void decisionMade(final int result) throws DecisionResultInvalidException {
                                    action.appendEffect(
                                            new ModifyTotalWeaponDestinyEffect(action, result));
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }

}
