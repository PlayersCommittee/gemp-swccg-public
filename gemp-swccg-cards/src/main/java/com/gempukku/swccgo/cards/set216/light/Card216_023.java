package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlienRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien/Republic
 * Title: Chewbacca, Defender Of Kashyyyk
 */
public class Card216_023 extends AbstractAlienRepublic {
    public Card216_023() {
        super(Side.LIGHT, 1, 4, 6, 2, 7, "Chewbacca, Defender Of Kashyyyk", Uniqueness.UNIQUE);
        setLore("Wookiee scout. Volunteered for Han's Endor strike team. Keeps his distance, but doesn't look like he's keeping his distance. Always thinks with his stomach.");
        setGameText("[Pilot] 2. Your total power here is +1 for each opponent's character present. During battle, if all your ability here is provided by Yoda, smugglers, and/or Wookiees, may add one destiny to total power.");
        addPersona(Persona.CHEWIE);
        setSpecies(Species.WOOKIEE);
        addKeywords(Keyword.SCOUT);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalPowerModifier(self, Filters.here(self), new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.character)), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isAllAbilityInBattleProvidedBy(game, playerId, Filters.or(Filters.Yoda, Filters.smuggler, Filters.Wookiee))
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendEffect(new AddDestinyToTotalPowerEffect(action, 1, playerId));

            return Collections.singletonList(action);
        }

        return null;
    }
}
