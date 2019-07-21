package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card211_100 extends AbstractRebel {
        public Card211_100() {
            super(Side.LIGHT, 1, 5, 5, 6, 7, "Ahsoka Tano", Uniqueness.UNIQUE);
            setLore("Female Togruta.");
            setGameText("Subtracts 1 from opponent's battle destiny draws here. During any deploy phase, if a Padawan or a Sith character at an adjacent site, Ahsoka may move to that site (using landspeed) as a regular move. Immune to [Permanent Weapon] weapons and attrition < 5.");
            addIcons(Icon.PILOT, Icon.WARRIOR,Icon.WARRIOR);
            addKeywords(Keyword.FEMALE);
            setSpecies(Species.TOGRUTA);
            addPersona(Persona.AHSOKA);
            setTestingText("Ahsoka Tano\nSubtracts 1 from opponent's battle destiny draws here. During any deploy phase, if a Padawan or a Sith character at an adjacent site, Ahsoka may move to that site (using landspeed) as a regular move. Immune to [Permanent Weapon] weapons and attrition < 5.");
        }

        @Override
        protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
            List<Modifier> modifiers = new LinkedList<Modifier>();
            modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), -1, game.getDarkPlayer()));
            //it might be self, Filter.self
            modifiers.add(new MayNotBeTargetedByPermanentWeaponsModifier(self));
            modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
            return modifiers;
        }

        //new action: move
}