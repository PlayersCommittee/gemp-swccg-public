package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDefenseValueUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Well Guarded (V)
 */
public class Card221_002 extends AbstractNormalEffect {
    public Card221_002() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Well_Guarded, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most of Jabba's guards had been sold to the Hutt and were too scared (or too dumb) to leave. Jabba assigned his best guards to watch over his most prized possessions.");
        setGameText("Deploy on table. Your Gamorreans, Niktos, and Weequays are guards. Your guards are forfeit +1. Your alien leaders with your guard are defense value +1. During battle, may reduce a character's defense value by 1 for each of your guards present. [Immune to Alter.]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Gamorrean, Filters.Nikto, Filters.Weequay)), Keyword.GUARD));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.guard), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.your(self), Filters.alien_leader, Filters.with(self, Filters.and(Filters.your(self), Filters.guard))), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.guard, Filters.presentAt(Filters.battleLocation)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.character, Filters.canBeTargetedBy(self)))) {

            final int guardsPresent = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.guard, Filters.presentAt(Filters.battleLocation), Filters.participatingInBattle));

            if (guardsPresent > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce defense value of a character by "+guardsPresent);

                action.appendUsage(
                        new OncePerBattleEffect(action));

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a character to reduce its defense value", Filters.and(Filters.character, Filters.participatingInBattle)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                action.appendEffect(new ModifyDefenseValueUntilEndOfBattleEffect(action, finalTarget, -guardsPresent));
                            }
                        });
                    }
                });

                return Collections.singletonList(action);
            }
        }
        return null;
    }
}