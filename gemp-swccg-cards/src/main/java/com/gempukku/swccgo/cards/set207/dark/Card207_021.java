package com.gempukku.swccgo.cards.set207.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Imperial
 * Title: Grand Admiral Thrawn (V)
 */
public class Card207_021 extends AbstractImperial {
    public Card207_021() {
        super(Side.DARK, 1, 4, 4, 4, 7, Title.Thrawn, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The last remaining Grand Admiral. Found legendary Katana fleet of missing dreadnaughts. Military genius. Master of unorthodox tactics. Passionate collector of art. Leader.");
        setGameText("[Pilot] 3. If a Rebel just moved from here, your Imperials present may follow that character (using landspeed). While at a battleground site, functions as a general and game text of Admiral’s Orders is suspended.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_7);
        addPersona(Persona.THRAWN);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atBattlegroundSite = new AtCondition(self, Filters.battleground_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new KeywordModifier(self, atBattlegroundSite, Keyword.GENERAL));
        modifiers.add(new CancelsGameTextModifier(self, Filters.Admirals_Order, atBattlegroundSite));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.Rebel, Filters.here(self))) {
            MovedResult movedResult = (MovedResult) effectResult;
            final Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            Filter movableFilter = Filters.and(Filters.your(self), Filters.Imperial, Filters.present(self),
                    Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, false, 0, null, toLocation));
            if (GameConditions.canSpot(game, self, movableFilter)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setRepeatableTrigger(true);
                action.setText("Have Imperial follow Rebel");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose Imperial", movableFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                action.setActionMsg("Have " + GameUtils.getCardLink(character) + " move using landspeed to follow Rebel");
                                // Perform result(s)
                                action.appendEffect(
                                        new MoveCardUsingLandspeedEffect(action, playerId, character, false, toLocation));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
