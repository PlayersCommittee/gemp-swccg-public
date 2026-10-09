package com.gempukku.swccgo.cards.set227.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Droid
 * Title: Swarming Vulture Droid
 */
public class Card227_014 extends AbstractStarfighter {
    public Card227_014() {
        super(Side.DARK, 0, 1, null, null, 3, null, 3, "Swarming Vulture Droid", Uniqueness.RESTRICTED_6, ExpansionSet.SET_27, Rarity.V);
        setGameText("* Power = number of [Separatist] systems on table. While alone, you may not Force drain here. If just lost, may stack on Droid Racks. Immune to Short-range Fighters.");
        addIcons(Icon.EPISODE_I, Icon.SEPARATIST, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE, Icon.VIRTUAL_SET_27);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextPowerModifier(self, new OnTableEvaluator(self, Filters.and(Icon.SEPARATIST, Filters.system))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Short_Range_Fighters));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.sameLocation(self), new AloneCondition(self), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
            && GameConditions.canSpot(game, self, Filters.Droid_Racks)) {

            PhysicalCard droidRacksCard = Filters.findFirstActive(game, self, Filters.Droid_Racks);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack on Droid Racks");
            action.setActionMsg("Stack " + GameUtils.getCardLink(self) + " on Droid Racks");
            // Perform result(s)
            action.appendEffect(
                new StackOneCardFromLostPileEffect(action, self, droidRacksCard, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
