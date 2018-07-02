package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PlayCardOptionIdCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Yoda's Gimer Stick
 */
public class Card4_043 extends AbstractNormalEffect {
    public Card4_043() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Yoda's Gimer Stick", Uniqueness.UNIQUE);
        setLore("A symbol of the Jedi Master and his ancient wisdom. Speak softly you may but a big stick you must carry, yes.");
        setGameText("Deploy on Yoda. You may initiate battles and attacks where present. OR Use 2 Force to deploy on one of your characters of ability > 3. Immune to attrition. Where present, no battles or attacks may be initiated unless an opponent's character of ability > 3 present.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2, PlayCardOptionId.PLAY_CARD_OPTION_2));
        return modifiers;
    }

    @Override
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_1, PlayCardZoneOption.ATTACHED, "Deploy on Yoda"));
        playCardOptions.add(new PlayCardOption(PlayCardOptionId.PLAY_CARD_OPTION_2, PlayCardZoneOption.ATTACHED, "Deploy on your character of ability > 3"));
        return playCardOptions;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1)
            return Filters.Yoda;
        else if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_2)
            return Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3));
        else
            return Filters.none;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        if (playCardOptionId == PlayCardOptionId.PLAY_CARD_OPTION_1)
            return Filters.Yoda;
        else
            return Filters.any;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition playCardOptionId1 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_1);
        Condition playCardOptionId2 = new PlayCardOptionIdCondition(self, PlayCardOptionId.PLAY_CARD_OPTION_2);
        Condition unlessOpponentsCharacterOfAbilityMoreThanThreePresent = new UnlessCondition(new PresentCondition(self,
                Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(3))));
        Filter wherePresent = Filters.wherePresent(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateAttacksAtLocationModifier(self, wherePresent, playCardOptionId1, playerId));
        modifiers.add(new MayInitiateBattleAtLocationModifier(self, wherePresent, playCardOptionId1, playerId));
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, wherePresent, new AndCondition(playCardOptionId2, unlessOpponentsCharacterOfAbilityMoreThanThreePresent)));
        modifiers.add(new MayNotInitiateAttacksAtLocationModifier(self, wherePresent, new AndCondition(playCardOptionId2, unlessOpponentsCharacterOfAbilityMoreThanThreePresent)));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.hasAttached(self), playCardOptionId2));
        return modifiers;
    }
}