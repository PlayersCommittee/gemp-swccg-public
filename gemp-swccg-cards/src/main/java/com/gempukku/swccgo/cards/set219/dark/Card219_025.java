package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Unkar Plutt
 */
public class Card219_025 extends AbstractAlien {
    public Card219_025() {
        super(Side.DARK, 4, 2, 2, 2, 4, "Unkar Plutt", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLore("Crolute scavenger and thief.");
        setGameText("Your battle destiny draws here are +¼ for each device, droid, starship, vehicle, or weapon card here. " +
                    "During battle with an opponent's droid, adds one destiny to total power. Once per game, may retrieve a device or character weapon.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.SCAVENGER, Keyword.THIEF);
        setSpecies(Species.CROLUTE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new WithCondition(self, Filters.and(Filters.opponents(self.getOwner()), Filters.droid)), 1));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), new MultiplyEvaluator(0.25f, new HereEvaluator(self, Filters.or(Filters.device, Filters.droid, Filters.starship, Filters.vehicle, Filters.weapon))), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.UNKAR_PLUTT__RETRIEVE_DEVICE_OR_WEAPON;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a device or character weapon");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.or(Filters.device, Filters.character_weapon)));
            return Collections.singletonList(action);
        }
        return null;
    }
}