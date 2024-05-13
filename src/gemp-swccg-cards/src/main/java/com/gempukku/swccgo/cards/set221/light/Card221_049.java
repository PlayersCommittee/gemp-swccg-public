package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: At Peace (V)
 */
public class Card221_049 extends AbstractNormalEffect {
    public Card221_049() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "At Peace", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("To recover from the strenuous Jedi training routine and revitalize the mind and body, an apprentice must rest to be calm and at peace.");
        setGameText("Deploy on Dagobah system. Your Force retrieval is canceled. Your [Dagobah] characters deploy -1 to Dagobah. Your training destiny draws are +1. " +
                "Jedi Test #2 may not move. Jedi Test #4 searches your Reserve Deck for free. [Immune to Alter.]");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.DAGOBAH, Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dagobah_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.character, Icon.DAGOBAH), -1, Filters.Dagobah_location));
        modifiers.add(new EachTrainingDestinyModifier(self, Filters.any, 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test_2, ModifyGameTextType.JEDI_TEST_2__MAY_NOT_MOVE));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test_4, ModifyGameTextType.JEDI_TEST_4__SEARCHES_FOR_FREE));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isForceRetrievalJustInitiated(game, effectResult, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Cancel Force retrieval");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceRetrievalEffect(action));
            actions.add(action);
        }

        return actions;
    }
}
