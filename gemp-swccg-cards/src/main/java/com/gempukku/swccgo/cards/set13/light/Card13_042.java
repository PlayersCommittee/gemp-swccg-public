package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Republic
 * Title: Sio Bibble
 */
public class Card13_042 extends AbstractRepublic {
    public Card13_042() {
        super(Side.LIGHT, 3, 2, 2, 2, 5, "Sio Bibble", Uniqueness.UNIQUE);
        setPolitics(1);
        setLore("Governor of Naboo. After initially opposing Amidala in her campaign to become Naboo's elected ruler, Sio Bibble is now one of her most ardent supporters.");
        setGameText("Agenda: order. While at same site as Amidala, your Force generation is +2 here. While on Naboo, subtracts one from opponent's Force drains at adjacent sites, and your other Republic characters present are power +1.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.ORDER));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition onNaboo = new OnCondition(self, Title.Naboo);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.here(self), new AtSameSiteAsCondition(self, Filters.Amidala), 2, playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.adjacentSite(self), onNaboo, -1, opponent));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Republic,
                Filters.character, Filters.present(self)), onNaboo, 1));
        return modifiers;
    }
}
