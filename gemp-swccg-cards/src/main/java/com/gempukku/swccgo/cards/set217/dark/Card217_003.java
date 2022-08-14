package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Effect
 * Title: Begin Landing Your Troops & Fighters Straight Ahead
 */
public class Card217_003 extends AbstractNormalEffect {
    public Card217_003() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Begin Landing Your Troops & Fighters Straight Ahead", Uniqueness.UNIQUE);
        addComboCardTitles("Begin Landing Your Troops", "Fighters Straight Ahead");
        setGameText("Deploy on table. Your unique (•) Republic characters of ability < 4 and your [Trade Federation] starships are forfeit +2. " +
                "Nabrun Leids and Elis Helrot are limited to owner's move phase and exterior sites only. " +
                "Once per turn, may deploy an [Episode I] (or Coruscant) docking bay from Reserve Deck; reshuffle. " +
                "While you occupy Naboo system with a [Trade Federation] starship, your Force generation is +1 there. " +
                "[Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.unique, Filters.Republic_character, Filters.abilityLessThan(4)), Filters.and(Icon.TRADE_FEDERATION, Filters.starship))), 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Nabrun_Leids, Filters.Elis_Helrot), ModifyGameTextType.NABRUN_LEIDS_ELIS_HELROT__LIMIT_USAGE));
        modifiers.add(new ForceGenerationModifier(self, Filters.Naboo_system, new OccupiesWithCondition(self, self.getOwner(), Filters.Naboo_system, Filters.and(Icon.TRADE_FEDERATION, Filters.starship)), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BEGIN_LANDING_YOUR_TROOPS_FIGHTERS_STRAIGHT_AHEAD__DOWNLOAD_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy docking bay from Reserve Deck");
            action.setActionMsg("Deploy an [Episode I] (or Coruscant) docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.or(Icon.EPISODE_I, Filters.Coruscant_location), Filters.docking_bay), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}