package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 9
 * Type: Character
 * Subtype: Republic
 * Title: Commander Cody
 */
public class Card601_219 extends AbstractRepublic {
    public Card601_219() {
        super(Side.LIGHT, 3, 3, 3, 2, 5, "Commander Cody", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setArmor(4);
        setLore("Leader. Clone trooper.");
        setGameText("Deploys -1 to same site as Obi-Wan. [Separatist] cards are power and defense value -1 here. When in battle at a site with Rex (or two of your clones), may cancel one just drawn battle destiny.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.CLONE_ARMY, Icon.LEGACY_BLOCK_9);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.CLONE_TROOPER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.ObiWan)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter separatistCardsHere = Filters.and(Icon.SEPARATIST, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, separatistCardsHere, -1));
        modifiers.add(new DefenseValueModifier(self, separatistCardsHere, -1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleAt(game, self, Filters.site)
                && (GameConditions.isInBattleWith(game, self, Filters.Rex)
                || GameConditions.isInBattleWith(game, self, 2, Filters.and(Filters.your(self), Filters.clone)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
