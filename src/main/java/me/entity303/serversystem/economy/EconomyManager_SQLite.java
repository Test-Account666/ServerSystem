package me.entity303.serversystem.economy;

import me.entity303.serversystem.main.ServerSystem;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EconomyManager_SQLite extends AbstractEconomyManager_SQL {

    public EconomyManager_SQLite(String currencySingular, String currencyPlural, String startingMoney, String displayFormat, String moneyFormat,
                                 String separator, String thousands, ServerSystem plugin) {
        super(currencySingular, currencyPlural, startingMoney, displayFormat, moneyFormat, separator, thousands, plugin);
    }

    @Override
    protected boolean Initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException var2) {
            this._plugin.Error("Class not found in initialize(): " + var2);
            return false;
        }
    }

    @Override
    public void Open() {
        if (this._connection != null)
            try {
                if (!this._connection.isClosed())
                    return;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

        if (!this.Initialize())
            return;

        try {
            this._connection = DriverManager.getConnection("jdbc:sqlite:" + new File("plugins//ServerSystem", "economy.sqlite").getAbsolutePath());
        } catch (SQLException var2) {
            this._plugin.Error("Could not establish an SQLite connection, SQLException: " + var2.getMessage());
        }

    }

    @Override
    public void FetchTopTen() {

    }
}
