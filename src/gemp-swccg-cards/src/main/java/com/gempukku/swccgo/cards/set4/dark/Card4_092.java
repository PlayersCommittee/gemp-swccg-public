package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Alien
 * Title: Bossk
 */
public class Card4_092 extends AbstractAlien {
    public Card4_092() {
        super(Side.DARK, 1, 4, 4, 2, 3, "Bossk", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Male Trandoshan bounty hunter. Strong but clumsy. Extremely proud and arrogant. Suffered a humiliating defeat at the hands of Chewbacca and his partner Han Solo.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Hound's Tooth, draws one battle destiny if not able to otherwise. Adds 1 to attrition against opponent in battles at same site. While present, may reduce Chewie's forfeit to zero while here.");
        addPersona(Persona.BOSSK);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR);
        setSpecies(Species.TRANDOSHAN);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setMatchingStarshipFilter(Filters.Hounds_Tooth);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Hounds_Tooth), 1));
        modifiers.add(new AttritionModifier(self, new InBattleAtCondition(self, Filters.site), 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isPresent(game, self)) {
            final PhysicalCard chewie = Filters.findFirstActive(game, self,
                    Filters.and(Filters.Chewie, Filters.atSameLocation(self)));
            if (chewie != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Reduce " + GameUtils.getFullName(chewie) + "'s forfeit to 0");
                action.setActionMsg("Reduce " + GameUtils.getCardLink(chewie) + "'s forfeit to 0");
                // Allow response(s)
                action.appendEffect(
                        new ResetForfeitEffect(action, chewie, 0, new NotCondition(new PresentAtCondition(self, Filters.sameLocation(chewie)))));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
