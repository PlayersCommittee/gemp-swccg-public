package com.gempukku.swccgo.cards.set227.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeUsingLandspeedModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 27
 * Type: Effect
 * Title: Do, Or Do Not & Wise Advice (V)
 */
public class Card227_019 extends AbstractNormalEffect {
    public Card227_019() {
        super(Side.LIGHT, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Do, Or Do Not & Wise Advice", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        addComboCardTitles(Title.Do_Or_Do_Not, Title.Wise_Advice);
        setGameText("Deploy on table. Sense and Alter are Lost Interrupts. When any player makes a destiny draw for Sense or Alter, and that destiny draw is successful, that player loses 2 Force (may not be reduced). May [download] Bog Clearing. [Dagobah] Luke is a Padawan. Once per game, during a battle involving [Dagobah] Luke, may lose 1 Force to cancel a non-[Sense] Interrupt. Yoda and Padawans are immune to Imperial Barrier and Stunning Leader and may move free using landspeed. While she is alone, Jedi Ahsoka’s gametext is canceled. [Immune to Alter.]");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_SET_27);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LostInterruptModifier(self, Filters.or(Filters.Sense, Filters.Alter)));
        // character filter enforces non-characters becoming padawans
        Filter dagobahLuke = Filters.and(Icon.DAGOBAH, Filters.Luke, Filters.character);
        modifiers.add(new KeywordModifier(self, dagobahLuke, Keyword.PADAWAN));
        Filter padawansAndYoda = Filters.or(Filters.padawan, Filters.Yoda);
        modifiers.add(new ImmuneToTitleModifier(self, padawansAndYoda, Title.Imperial_Barrier));
        modifiers.add(new ImmuneToTitleModifier(self, padawansAndYoda, Title.Stunning_Leader));
        modifiers.add(new MovesForFreeUsingLandspeedModifier(self, padawansAndYoda));
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.Jedi, Filters.Ahsoka, Filters.alone)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.DO_OR_DO_NOT_WISE_ADVICE__DOWNLOAD_BOG_CLEARING;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Dagobah_Bog_Clearing)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Bog Clearing from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Dagobah_Bog_Clearing, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DO_OR_DO_NOT_WISE_ADVICE__CANCEL_INTERRUPT;

        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
            && GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Icon.DAGOBAH, Filters.Luke))
            && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.senseOrAlterDestinyDrawSuccessful(game, effectResult)) {
            final String playerId = effectResult.getPerformingPlayerId();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + playerId + " lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                new LoseForceEffect(action, playerId, 2, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
