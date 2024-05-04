package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Subtype: Immediate
 * Title: A Gift (V)
 */
public class Card221_003 extends AbstractImmediateEffect {
    public Card221_003() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.A_Gift, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("'As a token of my good will, I present to you a gift: these two droids. Both are hardworking and will serve you well.'");
        setGameText("If you just moved a droid to Audience Chamber, deploy on table. You may not cancel battles. Force loss from Or Be Destroyed may not be reduced and is limited to 3. While Han with your non-'hit' Chewie, Lando, or Luke, Han may not be targeted by weapons. [Immune to Control.]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movedToLocationBy(game, effectResult, playerId, Filters.and(Filters.droid, Filters.canBeTargetedBy(self)), Filters.Audience_Chamber)) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, null, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hanFilter = Filters.and(Filters.Han, Filters.with(self, Filters.and(Filters.your(self), Filters.not(Filters.hit), Filters.or(Filters.Chewie, Filters.Lando, Filters.Luke))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotCancelBattleModifier(self, null, self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Or_Be_Destroyed, ModifyGameTextType.OR_BE_DESTROYED__FORCE_LOSS_MAY_NOT_EXCEED_THREE_OR_BE_REDUCED));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, hanFilter));
        return modifiers;
    }
}