package com.gempukku.swccgo.cards.set101.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Character
 * Subtype: Imperial
 * Title: Vader
 */
public class Card101_005 extends AbstractImperial {
    public Card101_005() {
        super(Side.DARK, 1, 7, 4, 6, 5, Title.Vader, Uniqueness.UNIQUE, ExpansionSet.PREMIERE_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Sought to extinguish all Jedi. Former student of Obi-Wan Kenobi. Seduced by the dark side of the Force.");
        setGameText("Must deploy on Death Star, but may move elsewhere. May not be deployed if two or more of opponent's unique (•) characters on table. If in a losing battle, draw destiny. If destiny > 4, 'choke' (lose) one Imperial present (your choice).");
        addPersona(Persona.VADER);
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Death_Star;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployModifier(self, new OnTableCondition(self, 2, Filters.and(Filters.opponents(self), Filters.unique, Filters.character))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny");
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1, DestinyType.CHOKE_DESTINY) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny != null) {
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            }

                            if (totalDestiny != null && totalDestiny <= 4) {
                                gameState.sendMessage("Result: Failed");
                            }
                            else {
                                if (totalDestiny == null)
                                    gameState.sendMessage("Result: Successful due to failed destiny draw");
                                else
                                    gameState.sendMessage("Result: Successful");

                                Filter targetFilter = Filters.and(Filters.Imperial, Filters.present(self));
                                Set<TargetingReason> targetingReasons = new HashSet<>(Arrays.asList(TargetingReason.TO_BE_CHOKED, TargetingReason.TO_BE_LOST));
                                if (GameConditions.canTarget(game, self, targetingReasons, targetFilter)) {
                                    action.appendEffect(
                                            new ChooseCardToLoseFromTableEffect(action, playerId, TargetingReason.TO_BE_CHOKED, targetFilter));
                                }
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
