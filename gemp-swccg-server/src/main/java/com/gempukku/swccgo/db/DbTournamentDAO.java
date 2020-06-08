package com.gempukku.swccgo.db;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import com.gempukku.swccgo.tournament.TournamentQueueInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbTournamentDAO implements TournamentDAO {
    private DbAccess _dbAccess;

    public DbTournamentDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void addTournament(String tournamentId, String draftType, String tournamentName, String format, CollectionType collectionType, Tournament.Stage stage, String pairingMechanism, String prizesScheme, Date start) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into tournament (tournament_id, draft_type, name, format, collection, stage, pairing, start, round, prizes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try {
                    statement.setString(1, tournamentId);
                    statement.setString(2, draftType);
                    statement.setString(3, tournamentName);
                    statement.setString(4, format);
                    statement.setString(5, collectionType.getCode()+":"+collectionType.getFullName());
                    statement.setString(6, stage.name());
                    statement.setString(7, pairingMechanism);
                    statement.setLong(8, start.getTime());
                    statement.setInt(9, 0);
                    statement.setString(10, prizesScheme);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public TournamentInfo getTournamentById(String tournamentId) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select draft_type, name, format, collection, stage, pairing, round, prizes from tournament where tournament_id=?");
                try {
                    statement.setString(1, tournamentId);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next()) {
                            String[] collectionTypeStr = rs.getString(4).split(":", 2);
                            return new TournamentInfo(
                                    tournamentId, rs.getString(1), rs.getString(2), rs.getString(3),
                                    new CollectionType(collectionTypeStr[0], collectionTypeStr[1]), Tournament.Stage.valueOf(rs.getString(5)),
                                    rs.getString(6), rs.getString(8), rs.getInt(7));
                        } else
                            return null;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public List<TournamentInfo> getUnfinishedTournaments() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, draft_type, name, format, collection, stage, pairing, round, prizes from tournament where stage <> '"+ Tournament.Stage.FINISHED.name()+"'");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentInfo> result = new ArrayList<TournamentInfo>();
                        while (rs.next()) {
                            String[] collectionTypeStr = rs.getString(5).split(":", 2);
                            result.add(new TournamentInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                    new CollectionType(collectionTypeStr[0], collectionTypeStr[1]), Tournament.Stage.valueOf(rs.getString(6)),
                                    rs.getString(7), rs.getString(9), rs.getInt(8)));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public List<TournamentInfo> getFinishedTournamentsSince(long time) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, draft_type, name, format, collection, stage, pairing, round, prizes from tournament where stage = '"+ Tournament.Stage.FINISHED.name()+"' and start>?");
                try {
                    statement.setLong(1, time);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentInfo> result = new ArrayList<TournamentInfo>();
                         while (rs.next()) {
                            String[] collectionTypeStr = rs.getString(5).split(":", 2);
                            result.add(new TournamentInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                    new CollectionType(collectionTypeStr[0], collectionTypeStr[1]), Tournament.Stage.valueOf(rs.getString(6)),
                                    rs.getString(7), rs.getString(9), rs.getInt(8)));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateTournamentStage(String tournamentId, Tournament.Stage stage) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update tournament set stage=? where tournament_id=?");
                try {
                    statement.setString(1, stage.name());
                    statement.setString(2, tournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateTournamentRound(String tournamentId, int round) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update tournament set round=? where tournament_id=?");
                try {
                    statement.setInt(1, round);
                    statement.setString(2, tournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    public List<TournamentQueueInfo> getUnstartedScheduledTournamentQueues(long tillDate) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, name, format, start, cost, playoff, prizes, minimum_players from scheduled_tournament where started = 0 and start<=?");
                try {
                    statement.setLong(1, tillDate);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentQueueInfo> result = new ArrayList<TournamentQueueInfo>();
                        while (rs.next()) {
                            result.add(new TournamentQueueInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getLong(4),
                                    rs.getInt(5), rs.getString(6), rs.getString(7), rs.getInt(8)));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateScheduledTournamentStarted(String scheduledTournamentId) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update scheduled_tournament set started=1 where tournament_id=?");
                try {
                    statement.setString(1, scheduledTournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(DateUtils.parseDateWithHour("2013-01-05 17:30:00").getTime());
    }
}
