package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Onyx 2 (V)
 */
public class Card200_136 extends AbstractStarfighter {
    public Card200_136() {
        super(Side.DARK, 2, 2, 3, null, 4, 4, 3, Title.Onyx_2, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Part of limited production run of TIE defenders. Testing of the prototype defender indicated the need for a more powerful hyperdrive, which was added for this production model.");
        setGameText("May deploy as a 'react'. Permanent pilot provides ability of 2. When deployed (except as a 'react'), may play a Defensive Shield from under your Starting Effect (as if from hand).");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.ONYX_SQUADRON);
        addModelType(ModelType.TIE_DEFENDER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (!playCardResult.isAsReact()) {
                PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
                if (startingEffect != null) {
                    Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                    if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setText("Play a Defensive Shield");
                        // Choose target(s)
                        action.appendTargeting(
                                new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                    }
                                }
                        );
                        return Collections.singletonList(action);
                    }
                }
            }
        }
        return null;
    }
}
