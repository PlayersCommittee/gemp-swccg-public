package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Resistance
 * Title: Vice Admiral Holdo
 */
public class Card211_055 extends AbstractResistance {
    public Card211_055(){
        super(Side.LIGHT, 2, 3, 1, 3, 5, "Vice Admiral Holdo", Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setLore("Female leader.");
        setGameText("[Pilot] 2: any capital starship. During battle, if piloting a capital starship, may place it and Holdo out of play to add X to your total battle destiny, where X is that starship's armor + hyperspeed.");
        setPolitics(1);
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_11, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ADMIRAL, Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.capital_starship));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter capitalsWithHyperdrive = Filters.and(Filters.capital_starship, Filters.not(Filters.hasNoHyperdrive));

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isPiloting(game, self, capitalsWithHyperdrive)) {

            // Card action 2
            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Holdo and starship out of play");
            action.setActionMsg("Place Holdo and starship out of play");

            PhysicalCard capital = Filters.findFirstActive(game, self, Filters.and( Filters.hasPiloting(self), capitalsWithHyperdrive));
            final float armor = game.getModifiersQuerying().getArmor(game.getGameState(), capital);
            final float hyperspeed = game.getModifiersQuerying().getHyperspeed(game.getGameState(), capital);
            final float armorPlusHyperspeed = armor + hyperspeed;

            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, capital));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new TotalBattleDestinyModifier(self, armorPlusHyperspeed, playerId, true),
                            "Increase total battle destiny by " +  GuiUtils.formatAsString(armorPlusHyperspeed)));
            return Collections.singletonList(action);
        }
        return null;
    }
}