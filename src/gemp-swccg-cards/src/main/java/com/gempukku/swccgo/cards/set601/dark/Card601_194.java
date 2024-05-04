package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Effect
 * Title: You'll Be Dead!
 */
public class Card601_194 extends AbstractNormalEffect {
    public Card601_194() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Youll_Be_Dead, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setGameText("Deploy on table. Non-lightsaber weapons carried by your non-Dark Jedi characters may not be stolen. During your control phase, opponent loses 1 Force for each battleground site you control with a non-[Permanent Weapon] blaster present. Imperial Artillery is a Lost Interrupt. Immune to Alter.");
        addIcons(Icon.LEGACY_BLOCK_8);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter weapons = Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.attachedTo(Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.Dark_Jedi))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeStolenModifier(self, weapons));
        modifiers.add(new LostInterruptModifier(self, Filters.Imperial_Artillery));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.controls(playerId),
                    Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.blaster, Filters.not(Icon.PERMANENT_WEAPON)))));
            if (numForce > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground_site, Filters.controls(playerId),
                    Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.blaster, Filters.not(Icon.PERMANENT_WEAPON)))));
            if (numForce > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}