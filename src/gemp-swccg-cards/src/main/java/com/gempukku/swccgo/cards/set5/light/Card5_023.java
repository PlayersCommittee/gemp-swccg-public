package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardsFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Frozen Assets
 */
public class Card5_023 extends AbstractNormalEffect {
    public Card5_023() {
        // playCardZoneOption null = multiple play options
        super(Side.LIGHT, 5, null, Title.Frozen_Assets, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Molten carbonite is released into the chamber and then flash frozen, releasing a blast of air. The subject is instantly covered in the newly solidified material.");
        setGameText("Deploy on your side of table. At every site where there is a 'frozen' captive, your Rebels deploy -2 and are power +2 in battle. OR Deploy on top of opponent's Force Pile. Force below this card may not be drawn or used. Effect lost at end of opponent's next turn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deploy on your side of table"));
        // Force-pile mode needs engine support (face-up Effect in Force pile / frozen-block / UI stack /
        // Beggar-both / Slip Sliding Away within stack). OPPONENTS_FORCE_PILE currently adds a normal
        // face-down Force card and does not call startAffecting — see Chief ping.
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.OPPONENTS_FORCE_PILE, "Deploy on top of opponent's Force Pile"));
        return playCardOptions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition tableOption = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Filter sitesWithFrozenCaptive = Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.frozenCaptive);
        Filter yourRebels = Filters.and(Filters.your(self), Filters.Rebel);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourRebels, tableOption, -2, sitesWithFrozenCaptive));
        // Power +2 in battle at those sites
        modifiers.add(new PowerModifier(self,
                Filters.and(yourRebels, Filters.participatingInBattle, Filters.at(sitesWithFrozenCaptive)),
                tableOption, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Scaffold: lose from Force pile at end of opponent's next turn (first EndOfOpponentsTurn after
        // deploy-on-your-turn). Will not fire until Force-pile Effects are treated as affecting.
        Zone zone = self.getZone();
        if (self.getPlayCardOptionId() == PlayCardOptionId.PLAY_CARD_OPTION_2
                && TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && (zone == Zone.FORCE_PILE || zone == Zone.TOP_OF_FORCE_PILE)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make Frozen Assets lost");
            action.appendEffect(
                    new LoseCardsFromForcePileEffect(action, self.getOwner(), self.getZoneOwner(), Filters.sameCardId(self)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
