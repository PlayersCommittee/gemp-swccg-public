package com.gempukku.swccgo.db;

import com.gempukku.swccgo.common.ApplicationConfiguration;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;

/**
 * The database access class.
 */
public class DbAccess {
    private DataSource _dataSource;

    /**
     * Creates the database access class.
     */
    public DbAccess() {
        try {
            Class.forName(ApplicationConfiguration.getProperty("db.connection.class"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find the DB driver", e);
        }

        _dataSource = setupDataSource(ApplicationConfiguration.getProperty("db.connection.url"));
    }

    /**
     * Gets the data source.
     * @return the data source
     */
    public DataSource getDataSource() {
        return _dataSource;
    }

    /**
     * Sets up the data source.
     * @param connectURI the connection URI
     * @return the data source
     */
    private DataSource setupDataSource(String connectURI) {
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(connectURI, ApplicationConfiguration.getProperty("db.connection.username"),
                        ApplicationConfiguration.getProperty("db.connection.password"));

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        GenericObjectPool connectionPool =
                new GenericObjectPool();
        connectionPool.setTestOnBorrow(true);

        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, connectionPool, null, ApplicationConfiguration.getProperty("db.connection.validateQuery"), false, true);

        connectionPool.setFactory(poolableConnectionFactory);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //

        return new PoolingDataSource(connectionPool);
    }
}
