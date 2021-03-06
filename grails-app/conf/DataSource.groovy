import com.pms.PropertiesReader
import com.pms.SourceFinder

dataSource {
    driverClassName = PropertiesReader.getProperty("dataSource.driverClassName", PropertiesReader.CONFIG_FILE_DB)
    dialect         = PropertiesReader.getProperty("dataSource.dialect", PropertiesReader.CONFIG_FILE_DB);
}
dataSource_mis {
    driverClassName = PropertiesReader.getProperty("dataSource.driverClassName", PropertiesReader.CONFIG_FILE_DB)
    dialect         = PropertiesReader.getProperty("dataSource.dialect", PropertiesReader.CONFIG_FILE_DB);
}
dataSource_comn {
    driverClassName = PropertiesReader.getProperty("dataSource.driverClassName", PropertiesReader.CONFIG_FILE_DB)
    dialect         = PropertiesReader.getProperty("dataSource.dialect", PropertiesReader.CONFIG_FILE_DB);
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache        = false
    cache.provider_class         = 'net.sf.ehcache.hibernate.EhCacheProvider'
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.db.username", PropertiesReader.CONFIG_FILE_DB));
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.db.password", PropertiesReader.CONFIG_FILE_DB));
            logSql = false
            properties {
                jmxEnabled = true
                maxActive = 5
                maxIdle = 2
                minIdle = 2
                initialSize = 1
                minEvictableIdleTimeMillis=60000
                timeBetweenEvictionRunsMillis=60000
                maxWait = 10000
                maxAge = 10 * 60000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=false
                ignoreExceptionOnPreLoad = true
                validationQuery="SELECT 1"
                validationQueryTimeout = 3
                jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED // safe default
                // controls for leaked connections
                abandonWhenPercentageFull = 100 // settings are active only when pool is full
                removeAbandonedTimeout = 120000
                removeAbandoned = true
                // use JMX console to change this setting at runtime
                logAbandoned = false // causes stacktrace recording overhead, use only for debugging
            }
        }
        dataSource_mis {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.mis.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.mis.db.username", PropertiesReader.CONFIG_FILE_DB))
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.mis.db.password", PropertiesReader.CONFIG_FILE_DB))
            logSql = false
            properties {
                jmxEnabled = true
                maxActive = 5
                maxIdle = 2
                minIdle = 2
                initialSize = 1
                minEvictableIdleTimeMillis=60000
                timeBetweenEvictionRunsMillis=60000
                maxWait = 10000
                maxAge = 10 * 60000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=false
                ignoreExceptionOnPreLoad = true
                validationQuery="SELECT 1"
                validationQueryTimeout = 3
                jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED // safe default
                // controls for leaked connections
                abandonWhenPercentageFull = 100 // settings are active only when pool is full
                removeAbandonedTimeout = 120000
                removeAbandoned = true
                // use JMX console to change this setting at runtime
                logAbandoned = false // causes stacktrace recording overhead, use only for debugging
            }
        }
        dataSource_comn {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.comn.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.comn.db.username", PropertiesReader.CONFIG_FILE_DB))
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.comn.db.password", PropertiesReader.CONFIG_FILE_DB))
            logSql = false
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.db.username", PropertiesReader.CONFIG_FILE_DB))
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.db.password", PropertiesReader.CONFIG_FILE_DB))
            logSql = false
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
        dataSource_mis {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.mis.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.mis.db.username", PropertiesReader.CONFIG_FILE_DB))
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.mis.db.password", PropertiesReader.CONFIG_FILE_DB))
            logSql = false
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
        dataSource_comn {
            dbCreate    = "update"
            url         = PropertiesReader.getProperty("dataSource.comn.db.url", PropertiesReader.CONFIG_FILE_DB)
            username    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.comn.db.username", PropertiesReader.CONFIG_FILE_DB))
            password    = SourceFinder.findAppropriate(PropertiesReader.getProperty("dataSource.comn.db.password", PropertiesReader.CONFIG_FILE_DB))
            logSql = false
            properties {
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
}
