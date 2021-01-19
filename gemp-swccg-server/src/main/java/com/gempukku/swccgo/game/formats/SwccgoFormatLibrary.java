package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SwccgoFormatLibrary {
    private Map<String, SwccgFormat> _allFormats = new LinkedHashMap<String, SwccgFormat>();
    private Map<String, SwccgFormat> _hallFormats = new LinkedHashMap<String, SwccgFormat>();

    public SwccgoFormatLibrary(SwccgCardBlueprintLibrary library) {
        try {
            final InputStreamReader reader = new InputStreamReader(SwccgoFormatLibrary.class.getResourceAsStream("/swccgFormats.json"), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                JSONArray object = (JSONArray) parser.parse(reader);
                for (Object formatDefObj : object) {
                    JSONObject formatDef = (JSONObject) formatDefObj;
                    String formatCode = (String) formatDef.get("code");
                    String name = (String) formatDef.get("name");
                    Boolean downloadBattlegroundRule = (Boolean) formatDef.get("downloadBattlegroundRule");
                    if (downloadBattlegroundRule == null)
                        downloadBattlegroundRule = false;

                    Boolean jpSealedRule = (Boolean) formatDef.get("jpSealedRule");
                    if (jpSealedRule == null)
                        jpSealedRule = false;

                    Boolean playtesting = (Boolean) formatDef.get("playtesting");
                    if (playtesting == null)
                        playtesting = false;

                    final DefaultSwccgFormat format = new DefaultSwccgFormat(library, name, downloadBattlegroundRule, jpSealedRule, playtesting);

                    Long deckSize = (Long) formatDef.get("deckSize");
                    if (deckSize == null)
                        deckSize = 60L;
                    format.setRequiredDeckSize(deckSize.intValue());

                    JSONArray sets = (JSONArray) formatDef.get("set");
                    for (Object set : sets)
                        format.addValidSet(((Number) set).intValue());

                    JSONArray validCards = (JSONArray) formatDef.get("valid");
                    if (validCards != null)
                        for (Object valid : validCards) {
                            format.addValidCard((String) valid);
                        }

                    JSONArray bannedCards = (JSONArray) formatDef.get("banned");
                    if (bannedCards != null)
                        for (Object bannedCard : bannedCards) {
                            format.addBannedCard((String) bannedCard);
                        }

                    String bannedListLink = (String) formatDef.get("bannedListLink");
                    if (bannedListLink != null) {
                        format.addBannedListLink(bannedListLink);
                    }

                    JSONArray bannedIcons = (JSONArray) formatDef.get("bannedIcons");
                    if (bannedIcons != null)
                        for (Object bannedIcon : bannedIcons) {
                            format.addBannedIcon((String) bannedIcon);
                        }

                    JSONArray bannedRarities = (JSONArray) formatDef.get("bannedRarities");
                    if (bannedRarities != null)
                        for (Object bannedRarity: bannedRarities) {
                            format.addBannedRarity((String) bannedRarity);
                        }

                    JSONArray restrictedCards = (JSONArray) formatDef.get("restricted");
                    if (restrictedCards != null)
                        for (Object restricted : restrictedCards) {
                            format.addRestrictedCard((String) restricted);
                        }

                    _allFormats.put(formatCode, format);

                    Boolean hallFormat = (Boolean) formatDef.get("hall");
                    if (hallFormat == null)
                        hallFormat = true;
                    if (hallFormat)
                        _hallFormats.put(formatCode, format);
                }
            } catch (ParseException exp) {
                throw new RuntimeException("Problem loading Swccg formats", exp);
            } finally {
                reader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading Swccg formats", exp);
        }
    }

    public Map<String, SwccgFormat> getHallFormats() {
        return Collections.unmodifiableMap(_hallFormats);
    }

    public Map<String, SwccgFormat> getAllFormats() {
        return Collections.unmodifiableMap(_allFormats);
    }

    public SwccgFormat getFormat(String formatCode) {
        return _allFormats.get(formatCode);
    }
}
