package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Set: Set 10
 * Type: Character
 * Subtype: Republic
 * Title: Clone Trooper
 */

public class Card210_009 extends AbstractRepublic{
    public Card210_009() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, Title.Clone_Trooper);
        setArmor(3);
        setLore("");
        setGameText("Deploys free to same site as your [Clone Army]: general, leader, or Padawan. Once during battle, if firing a blaster, may cancel and redraw your just drawn weapon destiny. While with your clone, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.VIRTUAL_SET_10, Icon.EPISODE_I, Icon.WARRIOR, Icon.CLONE_ARMY);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self.getOwner()), Icon.CLONE_ARMY, Filters.or(Filters.general, Filters.leader, Filters.padawan)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition withyourclone = new WithCondition(self, Filters.and(Filters.your(self.getOwner()), Filters.clone));

        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, withyourclone, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.blaster, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

}
