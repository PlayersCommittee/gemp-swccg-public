package com.gempukku.swccgo.cards.set111.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ImprisonedOnlyCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Third Anthology)
 * Type: Character
 * Subtype: Rebel
 * Title: Prisoner 2187
 */
public class Card111_005 extends AbstractRebel {
    public Card111_005() {
        super(Side.LIGHT, 1, 0, 4, 3, 6, Title.Prisoner_2187, Uniqueness.UNIQUE, ExpansionSet.THIRD_ANTHOLOGY, Rarity.PM);
        setLore("Princess Leia Organa. Alderaanian senator. Targeted by Vader for capture and interrogation. The Dark Lord of the Sith wanted her alive.");
        setGameText("Deploys only if Rescue The Princess on table. May not be transferred while imprisoned. Adds 2 to your Force drains here. For remainder of game, your objective cannot be placed out of play and, if Leia not on table, flip Sometimes I Amaze Even Myself.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PREMIUM, Icon.WARRIOR);
        addKeywords(Keyword.SENATOR, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpotFromAllOnTable(game, Filters.Rescue_The_Princess);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTransferredModifier(self, new ImprisonedOnlyCondition(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), 2, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRemainderOfGameTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return getRemainderOfGameTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    private List<RequiredGameTextTriggerAction> getRemainderOfGameTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.PRISONER_2187__FOR_REMAINDER_OF_GAME_CHANGES;

        // Check condition(s)
        if (self.getWhileInPlayData() == null) {
            self.setWhileInPlayData(new WhileInPlayData());
            if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
                String playerId = self.getOwner();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText(null);
                action.skipInitialMessageAndAnimation();
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddUntilEndOfGameModifierEffect(action, new ModifyGameTextModifier(self, Filters.and(Filters.your(playerId), Filters.Objective),
                                ModifyGameTextType.RESCUE_THE_PRINCESS__CANNOT_BE_PLACED_OUT_OF_PLAY), null));
                final int permCardId = self.getPermanentCardId();
                action.appendEffect(
                        new AddUntilEndOfGameActionProxyEffect(action,
                                new AbstractActionProxy() {
                                    @Override
                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                        final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                        // Check condition(s)
                                        if (TriggerConditions.isTableChanged(game, effectResult)) {
                                            PhysicalCard objective = Filters.findFirstActive(game, self, Filters.Sometimes_I_Amaze_Even_Myself);
                                            if (objective != null
                                                    && GameConditions.canBeFlipped(game, objective)
                                                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Leia)) {

                                                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                action.setSingletonTrigger(true);
                                                action.setText("Flip " + GameUtils.getFullName(objective));
                                                action.setActionMsg("Flip " + GameUtils.getCardLink(objective));
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new FlipCardEffect(action, objective));
                                                actions.add(action);
                                            }
                                        }
                                        return actions;
                                    }
                                }
                        )
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
