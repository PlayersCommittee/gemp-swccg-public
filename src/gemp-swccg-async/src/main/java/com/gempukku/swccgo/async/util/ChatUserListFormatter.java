package com.gempukku.swccgo.async.util;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class ChatUserListFormatter {
    private ChatUserListFormatter() {
    }

    public static List<String> formatAndSortUsers(Collection<String> usersInRoom, PlayerDAO playerDao) {
        Set<String> users = new TreeSet<String>(new CaseInsensitiveStringComparator());
        if (usersInRoom == null) {
            return new ArrayList<String>(users);
        }

        for (String userInRoom : usersInRoom) {
            String formattedName = formatPlayerNameForChatList(userInRoom, playerDao);
            if (!formattedName.isEmpty()) {
                users.add(formattedName);
            }
        }
        return new ArrayList<String>(users);
    }

    private static String formatPlayerNameForChatList(String userInRoom, PlayerDAO playerDao) {
        if (userInRoom == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(userInRoom);

        if (playerDao != null) {
            final Player player = playerDao.getPlayer(userInRoom);
            if (player != null) {
                final List<Player.Type> playerTypes = Player.Type.getTypes(player.getType());
                if (playerTypes.contains(Player.Type.ADMIN)) {
                    sb.insert(0, "* ");
                } else {
                    if (playerTypes.contains(Player.Type.LEAGUE_ADMIN) || playerTypes.contains(Player.Type.PLAYTESTING_ADMIN)) {
                        sb.insert(0, " ");
                        if (playerTypes.contains(Player.Type.COMMENTATOR)) {
                            sb.insert(0, "&#231;");
                        }
                        if (playerTypes.contains(Player.Type.PLAYTESTER)) {
                            sb.insert(0, "&beta;");
                        }
                        sb.insert(0, "+");
                    } else {
                        if (playerTypes.contains(Player.Type.PLAYTESTER) || playerTypes.contains(Player.Type.COMMENTATOR)) {
                            sb.insert(0, " ");
                            if (playerTypes.contains(Player.Type.COMMENTATOR)) {
                                sb.insert(0, "&#231;");
                            }
                            if (playerTypes.contains(Player.Type.PLAYTESTER)) {
                                sb.insert(0, "&beta;");
                            }
                        }
                        sb.append(" ");
                    }
                }
            }
        }

        sb.setLength(Math.min(sb.length(), 40));
        return sb.toString().trim();
    }

    private static final class CaseInsensitiveStringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1.contains(" ") && !o2.contains(" ")) {
                return -1;
            }
            if (!o1.contains(" ") && o2.contains(" ")) {
                return 1;
            }

            if (!o1.contains(" ") && !o2.contains(" ")) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }

            String oneWithSubstitutions = o1.replace("*", "a").replace("+", "b").replace("&beta;", "c").replace("&#231;", "d").replace(" ", "z");
            String twoWithSubstitutions = o2.replace("*", "a").replace("+", "b").replace("&beta;", "c").replace("&#231;", "d").replace(" ", "z");
            return oneWithSubstitutions.toLowerCase().compareTo(twoWithSubstitutions.toLowerCase());
        }
    }
}
