package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataNotSetCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Character
 * Subtype: Alien
 * Title: Jodo Kast
 */
public class Card110_009 extends AbstractAlien {
    public Card110_009() {
        super(Side.DARK, 2, 4, 3, 3, 3, Title.Jodo, Uniqueness.UNIQUE, ExpansionSet.ENHANCED_JABBAS_PALACE, Rarity.PM);
        setArmor(5);
        setLore("Bounty Hunter and scout. Perfectionist. Equipped with mandalorian armor and a jet pack. He doesn't mind being mistaken for Boba Fett. Occasionally works for Black Sun.");
        setGameText("When in battle, if opponent draws more than one battle destiny, may cancel one. Once per turn, when firing a rifle or blaster, may target for free and add 2 to total weapon destiny. May be targeted by Hidden Weapons. May 'fly' (landspeed = 3).");
        addIcons(Icon.PREMIUM, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canCancelDestiny(game, playerId)) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (destinyDrawnResult.getNumDestinyDrawnSoFar() > 1) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Cancel opponent's destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new CancelDestinyEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (self.getWhileInPlayData() != null && TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireWeaponFiredByForFreeModifier(self, new InPlayDataNotSetCondition(self), Filters.or(Filters.rifle, Filters.blaster)));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Hidden_Weapons));
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        return modifiers;
    }
}


