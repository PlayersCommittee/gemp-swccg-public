package com.gempukku.swccgo.game;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import java.util.List;
/*
 * This interface represents a Gemp-Swccg format.
 *
 * A Gemp-Swccg format defines which cards are valid
 * to be used in a deck playing a game of that format.
 */
public interface SwccgFormat {
    boolean hasDownloadBattlegroundRule();
    boolean hasJpSealedRule();
    boolean isPlaytesting();
    String getName();
    void validateCard(String cardId, boolean skipIfNotExists) throws DeckInvalidException;
    void validateDeck(SwccgDeck deck) throws DeckInvalidException;
    List<Integer> getValidSets();
    List<String> getBannedCards();
    void addTenetsLink(String tenetsLink);
    String getTenetsLink();
    List<String> getBannedIcons();
    List<String> getBannedRarities();
    List<String> getRestrictedCards();
    List<String> getValidCards();
    int getRequiredDeckSize();
    int getDefaultGameTimerMinutes();
    List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat();
}
